package com.toycell.servicefee.mapper;

import com.toycell.servicefee.dto.FeeRuleRequest;
import com.toycell.servicefee.dto.FeeRuleResponse;
import com.toycell.servicefee.entity.FeeRule;
import org.springframework.stereotype.Component;

/**
 * FeeRule entity ve DTO dönüşümleri
 */
@Component
public class FeeRuleMapper {

    public FeeRule toEntity(FeeRuleRequest request) {
        FeeRule feeRule = new FeeRule();
        feeRule.setCurrency(request.getCurrency());
        feeRule.setMinAmount(request.getMinAmount());
        feeRule.setMaxAmount(request.getMaxAmount());
        feeRule.setFeePercentage(request.getFeePercentage());
        feeRule.setFixedFee(request.getFixedFee());
        feeRule.setMinFee(request.getMinFee());
        feeRule.setMaxFee(request.getMaxFee());
        feeRule.setActive(request.getActive() != null ? request.getActive() : true);
        feeRule.setRulePriority(request.getRulePriority() != null ? request.getRulePriority() : 0);
        return feeRule;
    }

    public void updateEntity(FeeRule feeRule, FeeRuleRequest request) {
        feeRule.setCurrency(request.getCurrency());
        feeRule.setMinAmount(request.getMinAmount());
        feeRule.setMaxAmount(request.getMaxAmount());
        feeRule.setFeePercentage(request.getFeePercentage());
        feeRule.setFixedFee(request.getFixedFee());
        feeRule.setMinFee(request.getMinFee());
        feeRule.setMaxFee(request.getMaxFee());
        feeRule.setActive(request.getActive() != null ? request.getActive() : feeRule.getActive());
        feeRule.setRulePriority(request.getRulePriority() != null ? request.getRulePriority() : feeRule.getRulePriority());
    }

    public FeeRuleResponse toResponse(FeeRule feeRule) {
        return FeeRuleResponse.builder()
                .id(feeRule.getId())
                .currency(feeRule.getCurrency())
                .minAmount(feeRule.getMinAmount())
                .maxAmount(feeRule.getMaxAmount())
                .feePercentage(feeRule.getFeePercentage())
                .fixedFee(feeRule.getFixedFee())
                .minFee(feeRule.getMinFee())
                .maxFee(feeRule.getMaxFee())
                .active(feeRule.getActive())
                .rulePriority(feeRule.getRulePriority())
                .createdAt(feeRule.getCreatedAt())
                .updatedAt(feeRule.getUpdatedAt())
                .build();
    }
}
