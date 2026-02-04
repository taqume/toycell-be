package com.toycell.servicetransaction.service;

import com.toycell.commondomain.enums.Currency;
import com.toycell.commondomain.enums.TransactionType;
import com.toycell.commonexception.enums.ErrorCode;
import com.toycell.commonexception.exception.BusinessException;
import com.toycell.servicetransaction.dto.TransactionRequest;
import com.toycell.servicetransaction.dto.TransactionResponse;
import com.toycell.servicetransaction.dto.TransactionStatisticsResponse;
import com.toycell.servicetransaction.entity.Transaction;
import com.toycell.servicetransaction.mapper.TransactionMapper;
import com.toycell.servicetransaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    /**
     * Yeni bir transaction kaydı oluşturur
     */
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Creating transaction for user: {}, type: {}, amount: {}",
                request.getUserId(), request.getType(), request.getAmount());

        Transaction transaction = transactionMapper.toEntity(request);
        Transaction saved = transactionRepository.save(transaction);

        log.info("Transaction created with ID: {}", saved.getId());
        return transactionMapper.toResponse(saved);
    }

    /**
     * Kullanıcının tüm işlemlerini getirir
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactions(Long userId, Pageable pageable) {
        log.info("Fetching transactions for user: {}", userId);

        Page<Transaction> transactions = transactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    /**
     * Kullanıcının belirli bir cüzdanındaki işlemlerini getirir
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getWalletTransactions(
            Long userId, Long walletId, Pageable pageable) {
        log.info("Fetching transactions for user: {}, wallet: {}", userId, walletId);

        Page<Transaction> transactions = transactionRepository
                .findByUserIdAndWalletIdOrderByCreatedAtDesc(userId, walletId, pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    /**
     * Kullanıcının belirli türdeki işlemlerini getirir
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactionsByType(
            Long userId, TransactionType type, Pageable pageable) {
        log.info("Fetching {} transactions for user: {}", type, userId);

        Page<Transaction> transactions = transactionRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    /**
     * Tarih aralığında işlemleri getirir
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactionsByDateRange(
            Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Fetching transactions for user: {} between {} and {}",
                userId, startDate, endDate);

        Page<Transaction> transactions = transactionRepository
                .findByUserIdAndDateRange(userId, startDate, endDate, pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    /**
     * Kullanıcının belirli para birimindeki işlemlerini getirir
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactionsByCurrency(
            Long userId, Currency currency, Pageable pageable) {
        log.info("Fetching {} transactions for user: {}", currency, userId);

        Page<Transaction> transactions = transactionRepository
                .findByUserIdAndCurrencyOrderByCreatedAtDesc(userId, currency, pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    /**
     * Transaction ID'ye göre işlemi getirir
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long id) {
        log.info("Fetching transaction with ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        return transactionMapper.toResponse(transaction);
    }

    /**
     * Reference ID'ye göre işlemleri getirir (Transfer için)
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByReference(String referenceId) {
        log.info("Fetching transactions with reference ID: {}", referenceId);

        List<Transaction> transactions = transactionRepository.findByReferenceId(referenceId);

        return transactions.stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kullanıcının işlem istatistiklerini hesaplar
     */
    @Transactional(readOnly = true)
    public TransactionStatisticsResponse getUserStatistics(Long userId) {
        log.info("Calculating statistics for user: {}", userId);

        long totalCount = transactionRepository.countByUserId(userId);
        long depositCount = transactionRepository.countByUserIdAndType(userId, TransactionType.DEPOSIT);
        long withdrawCount = transactionRepository.countByUserIdAndType(userId, TransactionType.WITHDRAWAL);
        long transferInCount = transactionRepository.countByUserIdAndType(userId, TransactionType.TRANSFER_IN);
        long transferOutCount = transactionRepository.countByUserIdAndType(userId, TransactionType.TRANSFER_OUT);

        BigDecimal totalDeposits = transactionRepository
                .sumAmountByUserIdAndType(userId, TransactionType.DEPOSIT);
        BigDecimal totalWithdrawals = transactionRepository
                .sumAmountByUserIdAndType(userId, TransactionType.WITHDRAWAL);
        BigDecimal totalTransfersIn = transactionRepository
                .sumAmountByUserIdAndType(userId, TransactionType.TRANSFER_IN);
        BigDecimal totalTransfersOut = transactionRepository
                .sumAmountByUserIdAndType(userId, TransactionType.TRANSFER_OUT);

        // Null kontrolü
        totalDeposits = totalDeposits != null ? totalDeposits : BigDecimal.ZERO;
        totalWithdrawals = totalWithdrawals != null ? totalWithdrawals : BigDecimal.ZERO;
        totalTransfersIn = totalTransfersIn != null ? totalTransfersIn : BigDecimal.ZERO;
        totalTransfersOut = totalTransfersOut != null ? totalTransfersOut : BigDecimal.ZERO;

        // Net bakiye hesaplama
        BigDecimal netBalance = totalDeposits
                .add(totalTransfersIn)
                .subtract(totalWithdrawals)
                .subtract(totalTransfersOut);

        return TransactionStatisticsResponse.builder()
                .totalTransactions(totalCount)
                .depositCount(depositCount)
                .withdrawCount(withdrawCount)
                .transferInCount(transferInCount)
                .transferOutCount(transferOutCount)
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .totalTransfersIn(totalTransfersIn)
                .totalTransfersOut(totalTransfersOut)
                .netBalance(netBalance)
                .build();
    }

    /**
     * Tarih aralığında kullanıcının istatistiklerini hesaplar
     */
    @Transactional(readOnly = true)
    public TransactionStatisticsResponse getUserStatisticsByDateRange(
            Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating statistics for user: {} between {} and {}", userId, startDate, endDate);

        BigDecimal totalDeposits = transactionRepository
                .sumAmountByUserIdAndTypeAndDateRange(userId, TransactionType.DEPOSIT, startDate, endDate);
        BigDecimal totalWithdrawals = transactionRepository
                .sumAmountByUserIdAndTypeAndDateRange(userId, TransactionType.WITHDRAWAL, startDate, endDate);
        BigDecimal totalTransfersIn = transactionRepository
                .sumAmountByUserIdAndTypeAndDateRange(userId, TransactionType.TRANSFER_IN, startDate, endDate);
        BigDecimal totalTransfersOut = transactionRepository
                .sumAmountByUserIdAndTypeAndDateRange(userId, TransactionType.TRANSFER_OUT, startDate, endDate);

        // Null kontrolü
        totalDeposits = totalDeposits != null ? totalDeposits : BigDecimal.ZERO;
        totalWithdrawals = totalWithdrawals != null ? totalWithdrawals : BigDecimal.ZERO;
        totalTransfersIn = totalTransfersIn != null ? totalTransfersIn : BigDecimal.ZERO;
        totalTransfersOut = totalTransfersOut != null ? totalTransfersOut : BigDecimal.ZERO;

        BigDecimal netBalance = totalDeposits
                .add(totalTransfersIn)
                .subtract(totalWithdrawals)
                .subtract(totalTransfersOut);

        return TransactionStatisticsResponse.builder()
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .totalTransfersIn(totalTransfersIn)
                .totalTransfersOut(totalTransfersOut)
                .netBalance(netBalance)
                .build();
    }
}
