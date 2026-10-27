package com.payflow.payflow.service;

import com.payflow.payflow.dto.TransactionRequestDTO;
import com.payflow.payflow.dto.TransactionResponseDTO;
import com.payflow.payflow.dto.TransactionStatusUpdateDTO;
import com.payflow.payflow.entity.Merchant;
import com.payflow.payflow.entity.Transaction;
import com.payflow.payflow.entity.TransactionStatus;
import com.payflow.payflow.exception.InvalidStatusTransitionException;
import com.payflow.payflow.exception.ResourceNotFoundException;
import com.payflow.payflow.kafka.PaymentEventProducer;
import com.payflow.payflow.repository.MerchantRepository;
import com.payflow.payflow.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentEventProducer paymentEventProducer;

    public TransactionService(TransactionRepository transactionRepository ,
                              MerchantRepository merchantRepository ,
                              PaymentEventProducer paymentEventProducer)
    {
        this.merchantRepository = merchantRepository;
        this.transactionRepository = transactionRepository;
        this.paymentEventProducer = paymentEventProducer;
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO)
    {
        Merchant merchant = merchantRepository.findById(requestDTO.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found by id "+ requestDTO.getMerchantId()));

        Transaction transaction = new Transaction();
        transaction.setMerchant(merchant);
        transaction.setAmount(requestDTO.getAmount());
        transaction.setCurrency(requestDTO.getCurrency());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        return new TransactionResponseDTO(saved.getId(), saved.getMerchant().getId(), saved.getAmount(),
                saved.getCurrency(), saved.getStatus(), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    /**
     * Updates a transaction's status, enforcing the allowed lifecycle:
     * PENDING -> SUCCESS or PENDING -> FAILED only. Final states cannot change.
     *
     * @param id the transaction's database ID
     * @param requestDTO the new status requested by the client
     * @return response DTO with the updated transaction
     * @throws ResourceNotFoundException if the transaction doesn't exist
     * @throws InvalidStatusTransitionException if the transition is not allowed
     */
    public TransactionResponseDTO updateTransactionStatus(Long id  , TransactionStatusUpdateDTO requestDTO)
    {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Transaction not found by id "+id));

        TransactionStatus current  = transaction.getStatus();
        TransactionStatus requested = requestDTO.getStatus();

        // SUCCESS and FAILED are final states — a completed payment must never change
        if(current != TransactionStatus.PENDING)
        {
            throw new InvalidStatusTransitionException("Transaction is already " + current + " and cannot be changed");
        }

        // A transaction can only move forward from PENDING, never "back" to PENDING
        if (requested == TransactionStatus.PENDING) {
            throw new InvalidStatusTransitionException("Transaction is already PENDING");
        }
        transaction.setStatus(requested);
        transaction.setUpdatedAt(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        // Publish event to Kafka after status change
        String event = String.format("{\"transactionId\": %d, \"merchantId\": %d, \"status\": \"%s\", \"amount\": %s}" ,
                saved.getId() , saved.getMerchant().getId() , saved.getStatus() , saved.getAmount());

        paymentEventProducer.publishPaymentEvent(event);

        return new TransactionResponseDTO(saved.getId(), saved.getMerchant().getId(), saved.getAmount(),
                saved.getCurrency(), saved.getStatus(), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    /**
     * Fetches a single transaction by its ID.
     *
     * @param id the transaction's database ID
     * @return response DTO with transaction details
     * @throws ResourceNotFoundException if no transaction exists with the given ID
     */

    public TransactionResponseDTO getTransactionById(Long id)
    {
       Transaction transaction =  transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id "+id));

       return new TransactionResponseDTO(transaction.getId(), transaction.getMerchant().getId() , transaction.getAmount() , transaction.getCurrency()
        , transaction.getStatus() , transaction.getCreatedAt() , transaction.getUpdatedAt());
    }


    /**
     * Fetches all transactions belonging to a specific merchant.
     *
     * @param merchantId the merchant's database ID
     * @return list of response DTOs for every transaction of that merchant
     * @throws ResourceNotFoundException if the merchant doesn't exist
     */

    public List<TransactionResponseDTO> getTransactionsByMerchant(Long merchantId)
    {
        if(!merchantRepository.existsById(merchantId))
        {
            throw new ResourceNotFoundException("Merchant not found with id: " + merchantId);
        }

        List<Transaction> transactions = transactionRepository.findByMerchantId(merchantId);
        List<TransactionResponseDTO> responseList = new ArrayList<>();

        for(Transaction transaction : transactions)
        {
            responseList.add(new TransactionResponseDTO(transaction.getId() , transaction.getMerchant().getId(),
                    transaction.getAmount(), transaction.getCurrency() , transaction.getStatus() , transaction.getCreatedAt()
            ,transaction.getUpdatedAt()));
        }

        return responseList;
    }
}
