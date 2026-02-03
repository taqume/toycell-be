package com.toycell.servicebalance.repository;

import com.toycell.commondomain.enums.Currency;
import com.toycell.servicebalance.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    List<Wallet> findByUserId(Long userId);
    
    Optional<Wallet> findByUserIdAndCurrency(Long userId, Currency currency);
    
    boolean existsByUserIdAndCurrency(Long userId, Currency currency);
}
