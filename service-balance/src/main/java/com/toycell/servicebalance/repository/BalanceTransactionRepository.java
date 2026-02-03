package com.toycell.servicebalance.repository;

import com.toycell.servicebalance.entity.BalanceTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransaction, Long> {
    
    Page<BalanceTransaction> findByUserId(Long userId, Pageable pageable);
    
    List<BalanceTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
    
    Page<BalanceTransaction> findByWalletId(Long walletId, Pageable pageable);
}
