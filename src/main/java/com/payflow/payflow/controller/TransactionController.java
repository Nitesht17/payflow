package com.payflow.payflow.controller;

import com.payflow.payflow.dto.TransactionRequestDTO;
import com.payflow.payflow.dto.TransactionResponseDTO;
import com.payflow.payflow.dto.TransactionStatusUpdateDTO;
import com.payflow.payflow.entity.Transaction;
import com.payflow.payflow.service.TransactionService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService)
    {
        this.transactionService=transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO requestDTO)
    {
        TransactionResponseDTO response = transactionService.createTransaction(requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the status of an existing transaction.
     * PATCH /api/transactions/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TransactionResponseDTO> updateTransactionStatus(@PathVariable Long id,
                                                                          @Valid @RequestBody TransactionStatusUpdateDTO requestDTO)
    {
        TransactionResponseDTO response = transactionService.updateTransactionStatus(id , requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id)
    {
        TransactionResponseDTO response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionByMerchant(@PathVariable Long merchantId)
    {
        List<TransactionResponseDTO> response = transactionService.getTransactionsByMerchant(merchantId);
        return ResponseEntity.ok(response);
    }
}
