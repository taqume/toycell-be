package com.toycell.servicetransaction.repository;

import com.toycell.commondomain.enums.Currency;
import com.toycell.commondomain.enums.TransactionType;
import com.toycell.servicetransaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Kullanıcının tüm işlemlerini getirir
     */
    Page<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Kullanıcının belirli bir cüzdanındaki işlemleri getirir
     */
    Page<Transaction> findByUserIdAndWalletIdOrderByCreatedAtDesc(
            Long userId, Long walletId, Pageable pageable);

    /**
     * Kullanıcının belirli bir türdeki işlemlerini getirir
     */
    Page<Transaction> findByUserIdAndTypeOrderByCreatedAtDesc(
            Long userId, TransactionType type, Pageable pageable);

    /**
     * Tarih aralığında işlemleri getirir
     */
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Kullanıcının belirli bir para birimindeki işlemlerini getirir
     */
    Page<Transaction> findByUserIdAndCurrencyOrderByCreatedAtDesc(
            Long userId, Currency currency, Pageable pageable);

    /**
     * Reference ID'ye göre işlemi bulur (Transfer için)
     */
    List<Transaction> findByReferenceId(String referenceId);

    /**
     * Kullanıcının toplam gelir/gider istatistiklerini hesaplar
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.userId = :userId AND t.type = :type")
    BigDecimal sumAmountByUserIdAndType(
            @Param("userId") Long userId,
            @Param("type") TransactionType type);

    /**
     * Kullanıcının belirli tarih aralığındaki toplam tutarını hesaplar
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
           "WHERE t.userId = :userId " +
           "AND t.type = :type " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Kullanıcının işlem sayısını hesaplar
     */
    long countByUserId(Long userId);

    /**
     * Kullanıcının belirli türdeki işlem sayısını hesaplar
     */
    long countByUserIdAndType(Long userId, TransactionType type);
}
