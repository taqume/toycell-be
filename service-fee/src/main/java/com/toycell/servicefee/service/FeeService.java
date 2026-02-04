package com.toycell.servicefee.service;

import com.toycell.commonexception.exception.BusinessException;
import com.toycell.commonexception.enums.ErrorCode;
import com.toycell.commondomain.enums.Currency;
import com.toycell.servicefee.dto.FeeCalculationRequest;
import com.toycell.servicefee.dto.FeeCalculationResponse;
import com.toycell.servicefee.dto.FeeRuleRequest;
import com.toycell.servicefee.dto.FeeRuleResponse;
import com.toycell.servicefee.entity.FeeRule;
import com.toycell.servicefee.mapper.FeeRuleMapper;
import com.toycell.servicefee.repository.FeeRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ücret hesaplama ve kural yönetimi servisi
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeeService {

    private final FeeRuleRepository feeRuleRepository;
    private final FeeRuleMapper feeRuleMapper;

    /**
     * Transfer ücreti hesaplar
     */
    @Transactional(readOnly = true)
    public FeeCalculationResponse calculateFee(FeeCalculationRequest request) {
        log.info("Calculating fee for amount: {} {}", request.getAmount(), request.getCurrency());

        // Geçerli kuralı bul
        FeeRule applicableRule = feeRuleRepository
                .findApplicableRule(request.getCurrency(), request.getAmount())
                .orElseGet(() -> getDefaultRule(request.getCurrency()));

        // Ücret hesapla
        BigDecimal feeAmount = calculateFeeAmount(request.getAmount(), applicableRule);
        BigDecimal totalAmount = request.getAmount().add(feeAmount);

        String feeDetails = String.format(
                "Fee: %.2f%% + %.2f %s (min: %.2f, max: %s)",
                applicableRule.getFeePercentage(),
                applicableRule.getFixedFee(),
                request.getCurrency(),
                applicableRule.getMinFee(),
                applicableRule.getMaxFee() != null ? applicableRule.getMaxFee().toString() : "unlimited"
        );

        log.info("Fee calculated: {} {} for amount {} {}", feeAmount, request.getCurrency(), 
                request.getAmount(), request.getCurrency());

        return FeeCalculationResponse.builder()
                .originalAmount(request.getAmount())
                .feeAmount(feeAmount)
                .totalAmount(totalAmount)
                .currency(request.getCurrency())
                .feeDetails(feeDetails)
                .feeRuleId(applicableRule.getId())
                .build();
    }

    /**
     * Ücret tutarını hesaplar
     */
    private BigDecimal calculateFeeAmount(BigDecimal amount, FeeRule rule) {
        // Yüzde hesaplama: amount * (feePercentage / 100)
        BigDecimal percentageFee = amount
                .multiply(rule.getFeePercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Sabit ücret ekle
        BigDecimal totalFee = percentageFee.add(rule.getFixedFee());

        // Minimum ücret kontrolü
        if (totalFee.compareTo(rule.getMinFee()) < 0) {
            totalFee = rule.getMinFee();
        }

        // Maximum ücret kontrolü
        if (rule.getMaxFee() != null && totalFee.compareTo(rule.getMaxFee()) > 0) {
            totalFee = rule.getMaxFee();
        }

        return totalFee.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Varsayılan kural döner (kural yoksa)
     */
    private FeeRule getDefaultRule(Currency currency) {
        log.warn("No fee rule found for currency: {}, using default", currency);
        FeeRule defaultRule = new FeeRule();
        defaultRule.setId(0L);
        defaultRule.setCurrency(currency);
        defaultRule.setMinAmount(BigDecimal.ZERO);
        defaultRule.setMaxAmount(null);
        defaultRule.setFeePercentage(BigDecimal.valueOf(1.0)); // %1
        defaultRule.setFixedFee(BigDecimal.valueOf(1.0)); // 1 TRY/USD/EUR
        defaultRule.setMinFee(BigDecimal.valueOf(1.0));
        defaultRule.setMaxFee(null);
        defaultRule.setActive(true);
        defaultRule.setRulePriority(0);
        return defaultRule;
    }

    /**
     * Yeni ücret kuralı oluşturur
     */
    @Transactional
    public FeeRuleResponse createFeeRule(FeeRuleRequest request) {
        log.info("Creating new fee rule for currency: {}", request.getCurrency());

        // Validasyon: maxAmount >= minAmount
        if (request.getMaxAmount() != null && 
            request.getMaxAmount().compareTo(request.getMinAmount()) < 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Maximum amount must be greater than or equal to minimum amount");
        }

        // Validasyon: maxFee >= minFee
        if (request.getMaxFee() != null && 
            request.getMaxFee().compareTo(request.getMinFee()) < 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Maximum fee must be greater than or equal to minimum fee");
        }

        FeeRule feeRule = feeRuleMapper.toEntity(request);
        feeRule = feeRuleRepository.save(feeRule);

        log.info("Fee rule created with ID: {}", feeRule.getId());
        return feeRuleMapper.toResponse(feeRule);
    }

    /**
     * Ücret kuralını günceller
     */
    @Transactional
    public FeeRuleResponse updateFeeRule(Long id, FeeRuleRequest request) {
        log.info("Updating fee rule with ID: {}", id);

        FeeRule feeRule = feeRuleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEE_RULE_NOT_FOUND));

        // Validasyonlar
        if (request.getMaxAmount() != null && 
            request.getMaxAmount().compareTo(request.getMinAmount()) < 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Maximum amount must be greater than or equal to minimum amount");
        }

        if (request.getMaxFee() != null && 
            request.getMaxFee().compareTo(request.getMinFee()) < 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Maximum fee must be greater than or equal to minimum fee");
        }

        feeRuleMapper.updateEntity(feeRule, request);
        feeRule = feeRuleRepository.save(feeRule);

        log.info("Fee rule updated: {}", id);
        return feeRuleMapper.toResponse(feeRule);
    }

    /**
     * Ücret kuralını siler
     */
    @Transactional
    public void deleteFeeRule(Long id) {
        log.info("Deleting fee rule with ID: {}", id);

        if (!feeRuleRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.FEE_RULE_NOT_FOUND);
        }

        feeRuleRepository.deleteById(id);
        log.info("Fee rule deleted: {}", id);
    }

    /**
     * ID'ye göre ücret kuralını getirir
     */
    @Transactional(readOnly = true)
    public FeeRuleResponse getFeeRule(Long id) {
        FeeRule feeRule = feeRuleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEE_RULE_NOT_FOUND));

        return feeRuleMapper.toResponse(feeRule);
    }

    /**
     * Para birimine göre aktif kuralları getirir
     */
    @Transactional(readOnly = true)
    public List<FeeRuleResponse> getFeeRulesByCurrency(Currency currency) {
        return feeRuleRepository
                .findByCurrencyAndActiveOrderByRulePriorityDescMinAmountAsc(currency, true)
                .stream()
                .map(feeRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tüm aktif kuralları getirir
     */
    @Transactional(readOnly = true)
    public List<FeeRuleResponse> getAllActiveFeeRules() {
        return feeRuleRepository
                .findByActiveOrderByRulePriorityDescMinAmountAsc(true)
                .stream()
                .map(feeRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tüm kuralları getirir (aktif/pasif)
     */
    @Transactional(readOnly = true)
    public List<FeeRuleResponse> getAllFeeRules() {
        return feeRuleRepository.findAll()
                .stream()
                .map(feeRuleMapper::toResponse)
                .collect(Collectors.toList());
    }
}
