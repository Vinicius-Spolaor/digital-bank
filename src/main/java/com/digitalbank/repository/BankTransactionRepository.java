package com.digitalbank.repository;

import com.digitalbank.entity.BankTransaction;
import com.digitalbank.entity.enums.BankTransactionStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByOriginCustomerId(Long originCustomer);

    List<BankTransaction> findByDestinationCustomerId(Long destinationCustomer);

    List<BankTransaction> findByStatus(BankTransactionStatusEnum status);

    Page<BankTransaction> findByOriginCustomerIdOrDestinationCustomerId(
            Long originCustomer,
            Long destinationCustomer,
            Pageable pageable);

    @Query("SELECT t " +
            "FROM BankTransaction t " +
            "WHERE (t.originCustomer.id = :customerId OR t.destinationCustomer.id = :customerId) " +
            "AND t.transactionDate BETWEEN :initialDate AND :finalDate")
    List<BankTransaction> findByCustomerAndPeriod(
            @Param("customerId") Long customerId,
            @Param("initialDate") Instant initialDate,
            @Param("finalDate") Instant finalDate);
}
