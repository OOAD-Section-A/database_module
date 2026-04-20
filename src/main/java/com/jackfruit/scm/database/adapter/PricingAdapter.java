package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.PricingModels.BundlePromotion;
import com.jackfruit.scm.database.model.PricingModels.BundlePromotionSku;
import com.jackfruit.scm.database.model.PricingModels.ContractPricing;
import com.jackfruit.scm.database.model.PricingModels.CustomerSegmentation;
import com.jackfruit.scm.database.model.PricingModels.CustomerTierCache;
import com.jackfruit.scm.database.model.PricingModels.CustomerTierOverride;
import com.jackfruit.scm.database.model.PricingModels.DiscountPolicy;
import com.jackfruit.scm.database.model.PricingModels.DiscountRuleResult;
import com.jackfruit.scm.database.model.PricingModels.PriceApproval;
import com.jackfruit.scm.database.model.PricingModels.PriceConfiguration;
import com.jackfruit.scm.database.model.PricingModels.Promotion;
import com.jackfruit.scm.database.model.PricingModels.RebateProgram;
import com.jackfruit.scm.database.model.PricingModels.RegionalPricingMultiplier;
import com.jackfruit.scm.database.model.PricingModels.TierDefinition;
import com.jackfruit.scm.database.model.PricingModels.VolumeDiscountSchedule;
import com.jackfruit.scm.database.model.PricingModels.VolumeTierRule;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PricingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public PricingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void publishPrice(PriceList priceList) {
        facade.pricing().publishPrice(priceList);
    }

    public void createTierDefinition(TierDefinition tierDefinition) {
        facade.pricing().createTierDefinition(tierDefinition);
    }

    public void deleteTierDefinition(int tierId) {
        facade.pricing().deleteTierDefinition(tierId);
    }

    public void createCustomerSegmentation(CustomerSegmentation segmentation) {
        facade.pricing().createCustomerSegmentation(segmentation);
    }

    public void deleteCustomerSegmentation(String customerId) {
        facade.pricing().deleteCustomerSegmentation(customerId);
    }

    public void createPriceConfiguration(PriceConfiguration priceConfiguration) {
        facade.pricing().createPriceConfiguration(priceConfiguration);
    }

    public void deletePriceConfiguration(String priceConfigId) {
        facade.pricing().deletePriceConfiguration(priceConfigId);
    }

    public void createDiscountRuleResult(DiscountRuleResult discountRuleResult) {
        facade.pricing().createDiscountRuleResult(discountRuleResult);
    }

    public void deleteDiscountRuleResult(String orderLineId) {
        facade.pricing().deleteDiscountRuleResult(orderLineId);
    }

    public void createPromotion(Promotion promotion) {
        facade.pricing().createPromotion(promotion);
    }

    public void createDiscountPolicy(DiscountPolicy discountPolicy) {
        facade.pricing().createDiscountPolicy(discountPolicy);
    }

    public void deleteDiscountPolicy(String policyId) {
        facade.pricing().deleteDiscountPolicy(policyId);
    }

    public void createContractPricing(ContractPricing contractPricing) {
        facade.pricing().createContractPricing(contractPricing);
    }

    public void createPriceApproval(PriceApproval priceApproval) {
        facade.pricing().createPriceApproval(priceApproval);
    }

    public void deletePriceApproval(String approvalId) {
        facade.pricing().deletePriceApproval(approvalId);
    }

    public void createRebateProgram(RebateProgram rebateProgram) {
        facade.pricing().createRebateProgram(rebateProgram);
    }

    public void createBundlePromotion(BundlePromotion bundlePromotion) {
        facade.pricing().createBundlePromotion(bundlePromotion);
    }

    public void createBundlePromotionSku(BundlePromotionSku bundlePromotionSku) {
        facade.pricing().createBundlePromotionSku(bundlePromotionSku);
    }

    public void deleteBundlePromotionSku(String promoId, String skuId) {
        facade.pricing().deleteBundlePromotionSku(promoId, skuId);
    }

    public void createVolumeDiscountSchedule(VolumeDiscountSchedule schedule) {
        facade.pricing().createVolumeDiscountSchedule(schedule);
    }

    public void createVolumeTierRule(VolumeTierRule tierRule) {
        facade.pricing().createVolumeTierRule(tierRule);
    }

    public void deleteVolumeTierRule(long id) {
        facade.pricing().deleteVolumeTierRule(id);
    }

    public void createCustomerTierCache(CustomerTierCache tierCache) {
        facade.pricing().createCustomerTierCache(tierCache);
    }

    public void deleteCustomerTierCache(String customerId) {
        facade.pricing().deleteCustomerTierCache(customerId);
    }

    public void createCustomerTierOverride(CustomerTierOverride tierOverride) {
        facade.pricing().createCustomerTierOverride(tierOverride);
    }

    public void deleteCustomerTierOverride(String customerId) {
        facade.pricing().deleteCustomerTierOverride(customerId);
    }

    public void createRegionalPricingMultiplier(RegionalPricingMultiplier multiplier) {
        facade.pricing().createRegionalPricingMultiplier(multiplier);
    }

    public void deleteRegionalPricingMultiplier(String regionCode) {
        facade.pricing().deleteRegionalPricingMultiplier(regionCode);
    }

    public Optional<PriceList> getPrice(String priceId) {
        return facade.pricing().getPrice(priceId);
    }

    public Optional<TierDefinition> getTierDefinition(int tierId) {
        return facade.pricing().getTierDefinition(tierId);
    }

    public Optional<CustomerSegmentation> getCustomerSegmentation(String customerId) {
        return facade.pricing().getCustomerSegmentation(customerId);
    }

    public Optional<PriceConfiguration> getPriceConfiguration(String priceConfigId) {
        return facade.pricing().getPriceConfiguration(priceConfigId);
    }

    public Optional<Promotion> getPromotion(String promoId) {
        return facade.pricing().getPromotion(promoId);
    }

    public Optional<Promotion> getPromotionByCouponCode(String couponCode) {
        return facade.pricing().getPromotionByCouponCode(couponCode);
    }

    public Optional<DiscountPolicy> getDiscountPolicy(String policyId) {
        return facade.pricing().getDiscountPolicy(policyId);
    }

    public Optional<ContractPricing> getContractPricing(String contractId) {
        return facade.pricing().getContractPricing(contractId);
    }

    public Optional<PriceApproval> getPriceApproval(String approvalId) {
        return facade.pricing().getPriceApproval(approvalId);
    }

    public Optional<RebateProgram> getRebateProgram(String programId) {
        return facade.pricing().getRebateProgram(programId);
    }

    public Optional<VolumeDiscountSchedule> getVolumeDiscountSchedule(String scheduleId) {
        return facade.pricing().getVolumeDiscountSchedule(scheduleId);
    }

    public Optional<BundlePromotion> getBundlePromotion(String promoId) {
        return facade.pricing().getBundlePromotion(promoId);
    }

    public Optional<CustomerTierCache> getCustomerTierCache(String customerId) {
        return facade.pricing().getCustomerTierCache(customerId);
    }

    public Optional<RegionalPricingMultiplier> getRegionalMultiplier(String regionCode) {
        return facade.pricing().getRegionalMultiplier(regionCode);
    }

    public List<PriceList> getPricesBySku(String skuId) {
        return facade.pricing().getPricesBySku(skuId);
    }

    public List<PriceList> getPricesByRegion(String regionCode) {
        return facade.pricing().getPricesByRegion(regionCode);
    }

    public List<PriceList> getActivePrices() {
        return facade.pricing().getActivePrices();
    }

    public List<Promotion> listActivePromotions() {
        return facade.pricing().listActivePromotions();
    }

    public List<Promotion> listPromotionsBySku(String skuId) {
        return facade.pricing().listPromotionsBySku(skuId);
    }

    public List<Promotion> listExpiredPromotions() {
        return facade.pricing().listExpiredPromotions();
    }

    public List<PriceApproval> listPendingApprovals() {
        return facade.pricing().listPendingApprovals();
    }

    public List<PriceApproval> listApprovalsByStatus(String status) {
        return facade.pricing().listApprovalsByStatus(status);
    }

    public List<PriceApproval> listApprovalsByRequestedBy(String employeeId) {
        return facade.pricing().listApprovalsByRequestedBy(employeeId);
    }

    public List<RebateProgram> listRebateProgramsByCustomer(String customerId) {
        return facade.pricing().listRebateProgramsByCustomer(customerId);
    }

    public List<RebateProgram> listRebateProgramsBySku(String skuId) {
        return facade.pricing().listRebateProgramsBySku(skuId);
    }

    public List<VolumeDiscountSchedule> listVolumeDiscountSchedules() {
        return facade.pricing().listVolumeDiscountSchedules();
    }

    public List<VolumeTierRule> getVolumeTierRules(String scheduleId) {
        return facade.pricing().getVolumeTierRules(scheduleId);
    }

    public List<TierDefinition> listAllTierDefinitions() {
        return facade.pricing().listAllTierDefinitions();
    }

    public List<CustomerSegmentation> listCustomersInTier(int tierId) {
        return facade.pricing().listCustomersInTier(tierId);
    }

    public void updatePriceStatus(String priceId, String status) {
        facade.pricing().updatePriceStatus(priceId, status);
    }

    public void updateTierDefinition(TierDefinition tierDefinition) {
        facade.pricing().updateTierDefinition(tierDefinition);
    }

    public void updateCustomerSegmentation(CustomerSegmentation segmentation) {
        facade.pricing().updateCustomerSegmentation(segmentation);
    }

    public void updatePromotion(Promotion promotion) {
        facade.pricing().updatePromotion(promotion);
    }

    public void updatePromotionUseCount(String promoId, int newCount) {
        facade.pricing().updatePromotionUseCount(promoId, newCount);
    }

    public void updatePromotionExpired(String promoId, boolean expired) {
        facade.pricing().updatePromotionExpired(promoId, expired);
    }

    public void updatePriceApprovalStatus(String approvalId, String newStatus) {
        facade.pricing().updatePriceApprovalStatus(approvalId, newStatus);
    }

    public void updatePriceApprovalManager(String approvalId, String managerId) {
        facade.pricing().updatePriceApprovalManager(approvalId, managerId);
    }

    public void updateRebateAccumulatedSpend(String programId, BigDecimal newAmount) {
        facade.pricing().updateRebateAccumulatedSpend(programId, newAmount);
    }

    public void updateCustomerTierCache(CustomerTierCache tierCache) {
        facade.pricing().updateCustomerTierCache(tierCache);
    }

    public void updateCustomerTierOverride(CustomerTierOverride tierOverride) {
        facade.pricing().updateCustomerTierOverride(tierOverride);
    }

    public void updatePriceConfiguration(PriceConfiguration priceConfiguration) {
        facade.pricing().updatePriceConfiguration(priceConfiguration);
    }

    public void updateDiscountRuleResult(DiscountRuleResult discountRuleResult) {
        facade.pricing().updateDiscountRuleResult(discountRuleResult);
    }

    public void deletePromotion(String promoId) {
        facade.pricing().deletePromotion(promoId);
    }

    public void deletePrice(String priceId) {
        facade.pricing().deletePrice(priceId);
    }

    public void deleteContractPricing(String contractId) {
        facade.pricing().deleteContractPricing(contractId);
    }

    public void deleteVolumeDiscountSchedule(String scheduleId) {
        facade.pricing().deleteVolumeDiscountSchedule(scheduleId);
    }

    public void deleteBundlePromotion(String promoId) {
        facade.pricing().deleteBundlePromotion(promoId);
    }

    public void deleteRebateProgram(String programId) {
        facade.pricing().deleteRebateProgram(programId);
    }
}
