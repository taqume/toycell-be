package com.toycell.servicebalance.entity;

import com.toycell.commondomain.entity.BaseEntity;
import com.toycell.commondomain.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "balance_transactions", indexes = {
    @Index(name = "idx_wallet_id", columnList = "wallet_id"),
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(name = "base_seq_gen", sequenceName = "balance_transactions_seq", allocationSize = 50)
public class BalanceTransaction extends BaseEntity {

    @Column(nullable = false, name = "wallet_id")
    private Long walletId;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(length = 500)
    private String description;

    @Column(length = 100, name = "reference_id")
    private String referenceId;

    public enum TransactionType {
        DEPOSIT,    // Para yatırma
        WITHDRAW,   // Para çekme
        TRANSFER_IN,  // Transfer (gelen)
        TRANSFER_OUT  // Transfer (giden)
    }
}
