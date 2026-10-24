package com.payflow.payflow.repository;

import com.payflow.payflow.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository <Transaction , Long> {
    List<Transaction> findByMerchantId(Long merchantId);
}
