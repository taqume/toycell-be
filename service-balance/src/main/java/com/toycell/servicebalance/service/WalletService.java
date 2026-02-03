package com.toycell.servicebalance.service;

import com.toycell.commonexception.exception.BusinessException;
import com.toycell.commonexception.enums.ErrorCode;
import com.toycell.commondomain.enums.Currency;
import com.toycell.servicebalance.dto.*;
import com.toycell.servicebalance.entity.BalanceTransaction;
import com.toycell.servicebalance.entity.Wallet;
import com.toycell.servicebalance.repository.BalanceTransactionRepository;
import com.toycell.servicebalance.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final BalanceTransactionRepository transactionRepository;

    @Transactional
    public WalletResponse createWallet(Long userId, CreateWalletRequest request) {
        // Check if wallet already exists
        if (walletRepository.existsByUserIdAndCurrency(userId, request.getCurrency())) {
            throw new BusinessException(ErrorCode.WALLET_ALREADY_EXISTS);
        }

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .currency(request.getCurrency())
                .balance(BigDecimal.ZERO)
                .active(true)
                .build();

        wallet = walletRepository.save(wallet);
        log.info("Created wallet for user {} with currency {}", userId, request.getCurrency());

        return mapToResponse(wallet);
    }

    @Transactional(readOnly = true)
    public List<WalletResponse> getUserWallets(Long userId) {
        return walletRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));

        if (!wallet.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Unauthorized access to wallet");
        }

        return mapToResponse(wallet);
    }

    @Transactional
    public TransactionResponse deposit(Long userId, DepositRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));

        if (!wallet.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Unauthorized access to wallet");
        }

        if (!wallet.getActive()) {
            throw new BusinessException(ErrorCode.WALLET_INACTIVE);
        }

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.getAmount());
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        BalanceTransaction transaction = BalanceTransaction.builder()
                .walletId(wallet.getId())
                .userId(userId)
                .type(BalanceTransaction.TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .currency(wallet.getCurrency())
                .description(request.getDescription())
                .referenceId(generateReferenceId())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Deposited {} to wallet {}, new balance: {}", request.getAmount(), wallet.getId(), balanceAfter);

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse withdraw(Long userId, WithdrawRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));

        if (!wallet.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Unauthorized access to wallet");
        }

        if (!wallet.getActive()) {
            throw new BusinessException(ErrorCode.WALLET_INACTIVE);
        }

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        BalanceTransaction transaction = BalanceTransaction.builder()
                .walletId(wallet.getId())
                .userId(userId)
                .type(BalanceTransaction.TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .currency(wallet.getCurrency())
                .description(request.getDescription())
                .referenceId(generateReferenceId())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Withdrew {} from wallet {}, new balance: {}", request.getAmount(), wallet.getId(), balanceAfter);

        return mapToTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getWalletTransactions(Long walletId, Long userId, Pageable pageable) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));

        if (!wallet.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Unauthorized access to wallet");
        }

        return transactionRepository.findByWalletId(walletId, pageable)
                .map(this::mapToTransactionResponse);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable)
                .map(this::mapToTransactionResponse);
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .active(wallet.getActive())
                .build();
    }

    private TransactionResponse mapToTransactionResponse(BalanceTransaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .walletId(transaction.getWalletId())
                .userId(transaction.getUserId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .referenceId(transaction.getReferenceId())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private String generateReferenceId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
