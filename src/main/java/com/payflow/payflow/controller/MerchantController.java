package com.payflow.payflow.controller;

import com.payflow.payflow.dto.MerchantRequestDTO;
import com.payflow.payflow.dto.MerchantResponseDTO;
import com.payflow.payflow.service.MerchantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST endpoints for Merchant-related operations.
 * Base path: /api/merchants
 */
@RestController
@RequestMapping("api/merchants")
public class MerchantController {

    private  final MerchantService merchantService;

    public MerchantController(MerchantService merchantService)
    {
        this.merchantService = merchantService;
    }

    @PostMapping
    public ResponseEntity<MerchantResponseDTO>createMerchant(@Valid @RequestBody MerchantRequestDTO requestDTO)
    {
        MerchantResponseDTO response = merchantService.createMerchant(requestDTO);
        return ResponseEntity.ok(response);
    }

    /*
    Fetch single merchant by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MerchantResponseDTO> getMerchantById(@PathVariable Long id)
    {
       MerchantResponseDTO response = merchantService.getMerchantById(id);
       return ResponseEntity.ok(response);
    }

    /**
     * Fetches all merchants
     * GET /api/merchants
     */
    @GetMapping
    public ResponseEntity<List<MerchantResponseDTO>> getAllMerchant()
    {
        List<MerchantResponseDTO> merchants = merchantService.getAllMerchant();
        return ResponseEntity.ok(merchants);
    }

    /**
     * Updates an existing Merchant's details.
     * PUT /api/merchant/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MerchantResponseDTO> updateMerchant(@PathVariable Long id , @Valid @RequestBody MerchantRequestDTO requestDTO)
    {
      MerchantResponseDTO response =  merchantService.updateMerchant(id , requestDTO);
      return ResponseEntity.ok(response);
    }


    /**
     * Deletes a merchant by ID.
     * DELETE /api/merchants/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id)
    {
        merchantService.deleteMerchant(id);
        return ResponseEntity.noContent().build();
    }
}
