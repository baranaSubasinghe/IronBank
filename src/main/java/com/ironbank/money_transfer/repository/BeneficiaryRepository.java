package com.ironbank.money_transfer.repository;

import com.ironbank.money_transfer.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    // Find all contacts added by a specific user ID
    List<Beneficiary> findByUserId(Long userId);
}