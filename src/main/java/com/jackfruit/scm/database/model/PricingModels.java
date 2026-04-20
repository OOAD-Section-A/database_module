package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PricingModels {

    private PricingModels() {
    }

    public record TierDefinition(Integer tierId, String tierName, BigDecimal minSpendThreshold,
                                 BigDecimal defaultDiscountPct) {
    }

    public record CustomerSegmentation(String segmentationId, String customerId, BigDecimal cumulativeSpend,
                                       BigDecimal historicalOrderTotals, Integer assignedTierId,
                                       boolean manualOverride, Integer overrideTierId) {
    }

    public record PriceConfiguration(String priceConfigId, String skuId, BigDecimal cogsValue,
                                     BigDecimal desiredMarginPct, BigDecimal computedBasePrice,
                                     String productAttributes, LocalDateTime createdAt) {
    }

    public record DiscountRuleResult(String orderLineId, String orderId, int quantity,
                                     LocalDateTime batchExpiryDate, BigDecimal finalPrice,
                                     BigDecimal appliedDiscountPct, String discountBreakdown,
                                     LocalDateTime computedAt) {
    }

    public record Promotion(String promoId, String promoName, String couponCode, String discountType,
                            BigDecimal discountValue, LocalDateTime startDate, LocalDateTime endDate,
                            String eligibleSkuIds, BigDecimal minCartValue, int maxUses, int currentUseCount,
                            boolean expired) {
        public Promotion(String promoId, String promoName, String couponCode, String discountType,
                         BigDecimal discountValue, LocalDateTime startDate, LocalDateTime endDate,
                         String eligibleSkuIds, BigDecimal minCartValue, int maxUses, int currentUseCount) {
            this(promoId, promoName, couponCode, discountType, discountValue, startDate, endDate,
                    eligibleSkuIds, minCartValue, maxUses, currentUseCount, false);
        }
    }

    public record DiscountPolicy(String policyId, String policyName, String stackingRule, int priorityLevel,
                                 BigDecimal maxDiscountCapPct, int perishabilityDays,
                                 BigDecimal clearanceDiscountPct, boolean active) {
        public DiscountPolicy(String policyId, String policyName, String stackingRule, int priorityLevel,
                              BigDecimal maxDiscountCapPct, int perishabilityDays,
                              BigDecimal clearanceDiscountPct) {
            this(policyId, policyName, stackingRule, priorityLevel, maxDiscountCapPct,
                    perishabilityDays, clearanceDiscountPct, true);
        }
    }

    public record ContractPricing(String contractId, String contractCustomerId, String contractSkuId,
                                  BigDecimal negotiatedPrice, LocalDateTime contractStartDate,
                                  LocalDateTime contractExpiryDate, String contractStatus) {
    }

    public record PriceApproval(String approvalId, String requestType, String requestedBy,
                                BigDecimal requestedDiscountAmount, String justificationText,
                                String approvingManagerId, String approvalStatus,
                                LocalDateTime approvalTimestamp, boolean auditLogFlag,
                                LocalDateTime createdAt) {
    }

    public record RebateProgram(String programId, String customerId, String skuId,
                                BigDecimal targetSpend, BigDecimal accumulatedSpend,
                                BigDecimal rebatePct) {
    }

    public record BundlePromotion(String promoId, String promoName, BigDecimal discountPct,
                                  LocalDate startDate, LocalDate endDate, boolean expired) {
    }

    public record BundlePromotionSku(Long id, String promoId, String skuId) {
    }

    public record VolumeDiscountSchedule(String scheduleId, String skuId) {
    }

    public record VolumeTierRule(Long id, String scheduleId, int minQty, int maxQty,
                                 BigDecimal discountPct) {
    }

    public record CustomerTierCache(String customerId, String tier, Instant evaluatedAt) {
    }

    public record CustomerTierOverride(String customerId, String overrideTier, Instant overrideSetAt) {
    }

    public record RegionalPricingMultiplier(String regionCode, BigDecimal multiplier) {
    }
}
