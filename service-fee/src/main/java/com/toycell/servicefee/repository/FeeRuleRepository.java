package com.toycell.servicefee.repository;

import com.toycell.commondomain.enums.Currency;
import com.toycell.servicefee.entity.FeeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRuleRepository extends JpaRepository<FeeRule, Long> {

    /**
     * Belirli bir para birimi ve tutar için geçerli ücret kuralını bulur
     */
    @Query("SELECT f FROM FeeRule f WHERE f.currency = :currency " +
            "AND f.active = true " +
            "AND f.minAmount <= :amount " +
            "AND (f.maxAmount IS NULL OR f.maxAmount >= :amount) " +
            "ORDER BY f.rulePriority DESC, f.minAmount DESC")
    Optional<FeeRule> findApplicableRule(
            @Param("currency") Currency currency,
            @Param("amount") BigDecimal amount
    );

    /**
     * Para birimine göre aktif kuralları getirir
     */
    List<FeeRule> findByCurrencyAndActiveOrderByRulePriorityDescMinAmountAsc(
            Currency currency, 
            Boolean active
    );

    /**
     * Tüm aktif kuralları getirir
     */
    List<FeeRule> findByActiveOrderByRulePriorityDescMinAmountAsc(Boolean active);
}
