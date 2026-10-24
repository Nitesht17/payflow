package com.payflow.payflow.service;

import com.payflow.payflow.dto.MerchantRequestDTO;
import com.payflow.payflow.dto.MerchantResponseDTO;
import com.payflow.payflow.dto.UserRequestDTO;
import com.payflow.payflow.entity.Merchant;
import com.payflow.payflow.exception.ResourceNotFoundException;
import com.payflow.payflow.repository.MerchantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



/**
 * Handles business logic for Merchant operations —
 * currently supports creating a new merchant with an auto-generated API key.
 */
@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository)

    {
        this.merchantRepository = merchantRepository;
    }

    @Transactional
    public MerchantResponseDTO createMerchant(MerchantRequestDTO requestDTO)
    {
        Merchant merchant = new Merchant();
        merchant.setName(requestDTO.getName());
        merchant.setApiKey(UUID.randomUUID().toString());
        merchant.setCreatedAt(LocalDateTime.now());

        Merchant saved = merchantRepository.save(merchant);

        return new MerchantResponseDTO(saved.getId(),
                saved.getName() ,
                saved.getApiKey() ,
                saved.getCreatedAt());
    }

    /**
     * Fetches a single merchant by their ID.
     * Result is cached in Redis — subsequent calls with the same ID
     * return the cached value without hitting the database.
     */

    @Cacheable(value = "merchants" , key = "#id")
    public MerchantResponseDTO getMerchantById(Long id)
    {
       Optional<Merchant> optional = merchantRepository.findById(id);
       if(optional.isEmpty())
       {
           throw new ResourceNotFoundException("Merchant not found by id "+id);
       }
       Merchant merchant = optional.get();
       return new MerchantResponseDTO(merchant.getId() , merchant.getName() , merchant.getApiKey() , merchant.getCreatedAt());
    }

/**
 * Fetches all users in the system.
 *
 * @return list of response DTOs for every user
 */
    public List<MerchantResponseDTO> getAllMerchant()
    {
        List<Merchant> merchants = merchantRepository.findAll();
        List<MerchantResponseDTO> responseList = new ArrayList<>();
        for(Merchant merchant : merchants)
        {
            responseList.add(new MerchantResponseDTO(merchant.getId() , merchant.getName() , merchant.getApiKey() , merchant.getCreatedAt()));
        }
        return responseList;
    }

/**
 * Updates an existing user's name.
 * */

    @CacheEvict(value = "merchants" , key = "#id")
    public MerchantResponseDTO updateMerchant(Long id , MerchantRequestDTO requestDTO)
    {
        Optional<Merchant> optionalMerchant = merchantRepository.findById(id);
        if(optionalMerchant.isEmpty())
        {
            throw new ResourceNotFoundException("Merchant not found by id "+id);
        }
        Merchant merchant = optionalMerchant.get();
        merchant.setName(requestDTO.getName());

        Merchant update = merchantRepository.save(merchant);

        return new MerchantResponseDTO(update.getId() , update.getName() , update.getApiKey() , update.getCreatedAt());
    }

    /**
     * Deletes a merchant by their ID.
     *
     * @param id the merchant's database ID
     * @throws ResourceNotFoundException if no merchant exists with the given ID
     */

    @CacheEvict(value = "merchants", key = "#id")
    public void deleteMerchant(Long id)
    {
        if(!merchantRepository.existsById(id))
        {
            throw new ResourceNotFoundException("Merchant not found by id "+id);
        }
        merchantRepository.deleteById(id);
    }
}
