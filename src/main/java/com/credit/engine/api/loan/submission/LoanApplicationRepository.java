package com.credit.engine.api.loan.submission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, String>, JpaSpecificationExecutor<LoanApplication> {
    Optional<LoanApplication> findBySsnNumber(String ssnNumber);
}
