package com.toycell.servicetransfer.service;

import com.toycell.commonexception.exception.BusinessException;
import com.toycell.commonexception.enums.ErrorCode;
import com.toycell.commondomain.enums.TransactionType;
import com.toycell.servicetransfer.client.BalanceClient;
import com.toycell.servicetransfer.client.FeeClient;
import com.toycell.servicetransfer.client.TransactionClient;
import com.toycell.servicetransfer.dto.TransferRequest;
import com.toycell.servicetransfer.dto.TransferResponse;
import com.toycell.servicetransfer.dto.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final BalanceClient balanceClient;
    private final FeeClient feeClient;
    private final TransactionClient transactionClient;

    @Transactional
    public TransferResponse transfer(TransferRequest request, Long senderUserId) {
        log.info("Starting transfer from wallet {} to wallet {}, amount: {}", 
                request.getSenderWalletId(), request.getReceiverWalletId(), request.getAmount());

        // 1. Validate wallets
        WalletResponse senderWallet = validateAndGetWallet(request.getSenderWalletId());
        WalletResponse receiverWallet = validateAndGetWalletInternal(request.getReceiverWalletId());

        // 2. Validate sender owns the wallet
        if (!senderWallet.getUserId().equals(senderUserId)) {
            log.error("User {} does not own wallet {}", senderUserId, request.getSenderWalletId());
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "You can only transfer from your own wallet");
        }

        // 3. Validate same currency
        if (!senderWallet.getCurrency().equals(receiverWallet.getCurrency())) {
            log.error("Currency mismatch: sender {}, receiver {}", 
                    senderWallet.getCurrency(), receiverWallet.getCurrency());
            throw new BusinessException(ErrorCode.CURRENCY_MISMATCH);
        }

        // 4. Validate not transferring to self
        if (request.getSenderWalletId().equals(request.getReceiverWalletId())) {
            log.error("Cannot transfer to the same wallet");
            throw new BusinessException(ErrorCode.SAME_USER_TRANSFER);
        }

        // 5. Calculate fee
        FeeResponse feeResponse = calculateFee(request.getAmount(), request.getCurrency());
        BigDecimal totalAmount = request.getAmount().add(feeResponse.getFeeAmount());

        // 6. Validate sufficient balance
        if (senderWallet.getBalance().compareTo(totalAmount) < 0) {
            log.error("Insufficient balance: required {}, available {}", 
                    totalAmount, senderWallet.getBalance());
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // Generate transfer reference ID
        String transferReferenceId = "TRF-" + UUID.randomUUID().toString();

        try {
            // 7. Withdraw from sender (amount + fee)
            BigDecimal senderBalanceBefore = senderWallet.getBalance();
            WalletResponse senderAfterWithdraw = withdraw(
                    request.getSenderWalletId(), 
                    totalAmount, 
                    request.getCurrency(),
                    "Transfer to wallet " + request.getReceiverWalletId() + " (including fee)"
            );

            // 8. Deposit to receiver (amount only, no fee)
            BigDecimal receiverBalanceBefore = receiverWallet.getBalance();
            WalletResponse receiverAfterDeposit = deposit(
                    request.getReceiverWalletId(), 
                    request.getAmount(), 
                    request.getCurrency(),
                    "Transfer from wallet " + request.getSenderWalletId()
            );

            // 9. Create transaction records
            Long senderTransactionId = createTransaction(
                    senderUserId,
                    request.getSenderWalletId(),
                    TransactionType.TRANSFER_OUT,
                    totalAmount,
                    request.getCurrency(),
                    senderBalanceBefore,
                    senderAfterWithdraw.getBalance(),
                    "Transfer to wallet " + request.getReceiverWalletId() + " (Fee: " + feeResponse.getFeeAmount() + ")",
                    transferReferenceId,
                    receiverWallet.getUserId()
            );

            Long receiverTransactionId = createTransaction(
                    receiverWallet.getUserId(),
                    request.getReceiverWalletId(),
                    TransactionType.TRANSFER_IN,
                    request.getAmount(),
                    request.getCurrency(),
                    receiverBalanceBefore,
                    receiverAfterDeposit.getBalance(),
                    "Transfer from wallet " + request.getSenderWalletId(),
                    transferReferenceId,
                    senderUserId
            );

            log.info("Transfer completed successfully: {}", transferReferenceId);

            return TransferResponse.builder()
                    .transferId(System.currentTimeMillis())
                    .senderWalletId(request.getSenderWalletId())
                    .receiverWalletId(request.getReceiverWalletId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .feeAmount(feeResponse.getFeeAmount())
                    .totalAmount(totalAmount)
                    .description(request.getDescription())
                    .transferDate(LocalDateTime.now())
                    .senderTransactionId(senderTransactionId)
                    .receiverTransactionId(receiverTransactionId)
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Transfer failed: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.TRANSFER_FAILED, "Transfer failed: " + e.getMessage());
        }
    }

    private WalletResponse validateAndGetWallet(Long walletId) {
        try {
            var response = balanceClient.getWallet(walletId);
            if (!response.isSuccess() || response.getData() == null) {
                throw new BusinessException(ErrorCode.WALLET_NOT_FOUND);
            }
            return response.getData();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get wallet {}: {}", walletId, e.getMessage());
            throw new BusinessException(ErrorCode.WALLET_NOT_FOUND);
        }
    }

    private WalletResponse validateAndGetWalletInternal(Long walletId) {
        try {
            var response = balanceClient.getWalletInternal(walletId);
            if (!response.isSuccess() || response.getData() == null) {
                throw new BusinessException(ErrorCode.WALLET_NOT_FOUND);
            }
            return response.getData();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get wallet {}: {}", walletId, e.getMessage());
            throw new BusinessException(ErrorCode.WALLET_NOT_FOUND);
        }
    }

    private FeeResponse calculateFee(BigDecimal amount, com.toycell.commondomain.enums.Currency currency) {
        try {
            var response = feeClient.calculateTransferFee(amount, currency);
            if (!response.isSuccess() || response.getData() == null) {
                throw new BusinessException(ErrorCode.FEE_CALCULATION_FAILED);
            }
            return response.getData();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to calculate fee: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FEE_CALCULATION_FAILED);
        }
    }

    private WalletResponse withdraw(Long walletId, BigDecimal amount, 
                                    com.toycell.commondomain.enums.Currency currency, String description) {
        try {
            WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                    .walletId(walletId)
                    .amount(amount)
                    .currency(currency)
                    .description(description)
                    .build();

            var response = balanceClient.withdraw(withdrawRequest);
            if (!response.isSuccess() || response.getData() == null) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
            }
            return response.getData();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to withdraw from wallet {}: {}", walletId, e.getMessage());
            throw new BusinessException(ErrorCode.TRANSFER_FAILED, "Withdrawal failed");
        }
    }

    private WalletResponse deposit(Long walletId, BigDecimal amount, 
                                   com.toycell.commondomain.enums.Currency currency, String description) {
        try {
            DepositRequest depositRequest = DepositRequest.builder()
                    .walletId(walletId)
                    .amount(amount)
                    .currency(currency)
                    .description(description)
                    .build();

            // Use internal endpoint for receiver wallet (no authentication needed)
            var response = balanceClient.depositInternal(depositRequest);
            if (!response.isSuccess() || response.getData() == null) {
                throw new BusinessException(ErrorCode.TRANSFER_FAILED);
            }
            return response.getData();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to deposit to wallet {}: {}", walletId, e.getMessage());
            throw new BusinessException(ErrorCode.TRANSFER_FAILED, "Deposit failed");
        }
    }

    private Long createTransaction(Long userId, Long walletId, TransactionType type,
                                  BigDecimal amount, com.toycell.commondomain.enums.Currency currency,
                                  BigDecimal balanceBefore, BigDecimal balanceAfter,
                                  String description, String referenceId, Long relatedUserId) {
        try {
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .userId(userId)
                    .walletId(walletId)
                    .type(type)
                    .amount(amount)
                    .currency(currency)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(balanceAfter)
                    .description(description)
                    .referenceId(referenceId)
                    .relatedUserId(relatedUserId)
                    .build();

            var response = transactionClient.createTransaction(transactionRequest);
            if (!response.isSuccess()) {
                log.warn("Failed to create transaction record, but transfer completed");
            }
            return System.currentTimeMillis();
        } catch (Exception e) {
            log.error("Failed to create transaction record: {}", e.getMessage());
            return null;
        }
    }
}
