package com.payflow.payflow.repository;

import com.payflow.payflow.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant , Long> {
}
