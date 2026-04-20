package com.jackfruit.scm.database.facade.subsystem;

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
import com.jackfruit.scm.database.service.JdbcOperations;
import com.jackfruit.scm.database.service.PricingService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PricingSubsystemFacade {

    private final PricingService pricingService;
    private final JdbcOperations jdbcOperations;

    public PricingSubsystemFacade(PricingService pricingService, JdbcOperations jdbcOperations) {
        this.pricingService = pricingService;
        this.jdbcOperations = jdbcOperations;
    }

    public void publishPrice(PriceList priceList) {
        pricingService.createPrice(priceList);
    }

    public Optional<PriceList> getPrice(String priceId) {
        return pricingService.getPrice(priceId);
    }

    public List<PriceList> listPrices() {
        return pricingService.getAllPrices();
    }

    public List<PriceList> getPricesBySku(String skuId) {
        return jdbcOperations.query(
                "SELECT * FROM price_list WHERE sku_id = ? ORDER BY effective_from DESC",
                (resultSet) -> mapPriceList(resultSet),
                statement -> statement.setString(1, skuId));
    }

    public List<PriceList> getPricesByRegion(String regionCode) {
        return jdbcOperations.query(
                "SELECT * FROM price_list WHERE region_code = ? ORDER BY effective_from DESC",
                (resultSet) -> mapPriceList(resultSet),
                statement -> statement.setString(1, regionCode));
    }

    public List<PriceList> getActivePrices() {
        return jdbcOperations.query(
                "SELECT * FROM price_list WHERE status = 'ACTIVE' ORDER BY effective_from DESC",
                this::mapPriceList);
    }

    public void updatePriceStatus(String priceId, String status) {
        jdbcOperations.update(
                "UPDATE price_list SET status = ? WHERE price_id = ?",
                statement -> {
                    statement.setString(1, status);
                    statement.setString(2, priceId);
                });
    }

    public void deletePrice(String priceId) {
        pricingService.deletePrice(priceId);
    }

    public void createTierDefinition(TierDefinition tierDefinition) {
        jdbcOperations.update(
                """
                INSERT INTO tier_definitions (tier_id, tier_name, min_spend_threshold, default_discount_pct)
                VALUES (?, ?, ?, ?)
                """,
                statement -> {
                    statement.setInt(1, tierDefinition.tierId());
                    statement.setString(2, tierDefinition.tierName());
                    statement.setBigDecimal(3, tierDefinition.minSpendThreshold());
                    statement.setBigDecimal(4, tierDefinition.defaultDiscountPct());
                });
    }

    public Optional<TierDefinition> getTierDefinition(int tierId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM tier_definitions WHERE tier_id = ?",
                resultSet -> new TierDefinition(
                        resultSet.getInt("tier_id"),
                        resultSet.getString("tier_name"),
                        resultSet.getBigDecimal("min_spend_threshold"),
                        resultSet.getBigDecimal("default_discount_pct")),
                statement -> statement.setInt(1, tierId));
    }

    public List<TierDefinition> listTierDefinitions() {
        return listAllTierDefinitions();
    }

    public List<TierDefinition> listAllTierDefinitions() {
        return jdbcOperations.query(
                "SELECT * FROM tier_definitions ORDER BY tier_id ASC",
                resultSet -> new TierDefinition(
                        resultSet.getInt("tier_id"),
                        resultSet.getString("tier_name"),
                        resultSet.getBigDecimal("min_spend_threshold"),
                        resultSet.getBigDecimal("default_discount_pct")));
    }

    public void updateTierDefinition(TierDefinition tierDefinition) {
        jdbcOperations.update(
                """
                UPDATE tier_definitions
                SET tier_name = ?, min_spend_threshold = ?, default_discount_pct = ?
                WHERE tier_id = ?
                """,
                statement -> {
                    statement.setString(1, tierDefinition.tierName());
                    statement.setBigDecimal(2, tierDefinition.minSpendThreshold());
                    statement.setBigDecimal(3, tierDefinition.defaultDiscountPct());
                    statement.setInt(4, tierDefinition.tierId());
                });
    }

    public void deleteTierDefinition(int tierId) {
        jdbcOperations.update(
                "DELETE FROM tier_definitions WHERE tier_id = ?",
                statement -> statement.setInt(1, tierId));
    }

    public void createCustomerSegmentation(CustomerSegmentation segmentation) {
        jdbcOperations.update(
                """
                INSERT INTO customer_segmentation
                (segmentation_id, customer_id, cumulative_spend, historical_order_totals, assigned_tier_id, manual_override, override_tier_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, segmentation.segmentationId());
                    statement.setString(2, segmentation.customerId());
                    statement.setBigDecimal(3, segmentation.cumulativeSpend());
                    statement.setBigDecimal(4, segmentation.historicalOrderTotals());
                    statement.setInt(5, segmentation.assignedTierId());
                    statement.setBoolean(6, segmentation.manualOverride());
                    setNullableInteger(statement, 7, segmentation.overrideTierId());
                });
    }

    public Optional<CustomerSegmentation> getCustomerSegmentation(String customerId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM customer_segmentation WHERE customer_id = ?",
                resultSet -> new CustomerSegmentation(
                        resultSet.getString("segmentation_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getBigDecimal("cumulative_spend"),
                        resultSet.getBigDecimal("historical_order_totals"),
                        resultSet.getInt("assigned_tier_id"),
                        resultSet.getBoolean("manual_override"),
                        getNullableInteger(resultSet.getInt("override_tier_id"), resultSet.wasNull())),
                statement -> statement.setString(1, customerId));
    }

    public List<CustomerSegmentation> listCustomersInTier(int tierId) {
        return jdbcOperations.query(
                "SELECT * FROM customer_segmentation WHERE assigned_tier_id = ? ORDER BY customer_id ASC",
                resultSet -> new CustomerSegmentation(
                        resultSet.getString("segmentation_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getBigDecimal("cumulative_spend"),
                        resultSet.getBigDecimal("historical_order_totals"),
                        resultSet.getInt("assigned_tier_id"),
                        resultSet.getBoolean("manual_override"),
                        getNullableInteger(resultSet.getInt("override_tier_id"), resultSet.wasNull())),
                statement -> statement.setInt(1, tierId));
    }

    public void updateCustomerSegmentation(CustomerSegmentation segmentation) {
        jdbcOperations.update(
                """
                UPDATE customer_segmentation
                SET cumulative_spend = ?, historical_order_totals = ?, assigned_tier_id = ?,
                    manual_override = ?, override_tier_id = ?
                WHERE customer_id = ?
                """,
                statement -> {
                    statement.setBigDecimal(1, segmentation.cumulativeSpend());
                    statement.setBigDecimal(2, segmentation.historicalOrderTotals());
                    statement.setInt(3, segmentation.assignedTierId());
                    statement.setBoolean(4, segmentation.manualOverride());
                    setNullableInteger(statement, 5, segmentation.overrideTierId());
                    statement.setString(6, segmentation.customerId());
                });
    }

    public void deleteCustomerSegmentation(String customerId) {
        jdbcOperations.update(
                "DELETE FROM customer_segmentation WHERE customer_id = ?",
                statement -> statement.setString(1, customerId));
    }

    public void createPriceConfiguration(PriceConfiguration priceConfiguration) {
        jdbcOperations.update(
                """
                INSERT INTO price_configuration
                (price_config_id, sku_id, cogs_value, desired_margin_pct, computed_base_price, product_attributes, created_at)
                VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                statement -> {
                    statement.setString(1, priceConfiguration.priceConfigId());
                    statement.setString(2, priceConfiguration.skuId());
                    statement.setBigDecimal(3, priceConfiguration.cogsValue());
                    statement.setBigDecimal(4, priceConfiguration.desiredMarginPct());
                    statement.setBigDecimal(5, priceConfiguration.computedBasePrice());
                    statement.setString(6, priceConfiguration.productAttributes());
                });
    }

    public Optional<PriceConfiguration> getPriceConfiguration(String priceConfigId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM price_configuration WHERE price_config_id = ?",
                resultSet -> new PriceConfiguration(
                        resultSet.getString("price_config_id"),
                        resultSet.getString("sku_id"),
                        resultSet.getBigDecimal("cogs_value"),
                        resultSet.getBigDecimal("desired_margin_pct"),
                        resultSet.getBigDecimal("computed_base_price"),
                        resultSet.getString("product_attributes"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()),
                statement -> statement.setString(1, priceConfigId));
    }

    public void updatePriceConfiguration(PriceConfiguration priceConfiguration) {
        jdbcOperations.update(
                """
                UPDATE price_configuration
                SET cogs_value = ?, desired_margin_pct = ?, computed_base_price = ?, product_attributes = ?
                WHERE price_config_id = ?
                """,
                statement -> {
                    statement.setBigDecimal(1, priceConfiguration.cogsValue());
                    statement.setBigDecimal(2, priceConfiguration.desiredMarginPct());
                    statement.setBigDecimal(3, priceConfiguration.computedBasePrice());
                    statement.setString(4, priceConfiguration.productAttributes());
                    statement.setString(5, priceConfiguration.priceConfigId());
                });
    }

    public void deletePriceConfiguration(String priceConfigId) {
        jdbcOperations.update(
                "DELETE FROM price_configuration WHERE price_config_id = ?",
                statement -> statement.setString(1, priceConfigId));
    }

    public void createDiscountRuleResult(DiscountRuleResult discountRuleResult) {
        jdbcOperations.update(
                """
                INSERT INTO discount_rule_results
                (order_line_id, order_id, quantity, batch_expiry_date, final_price, applied_discount_pct, discount_breakdown, computed_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                statement -> {
                    statement.setString(1, discountRuleResult.orderLineId());
                    statement.setString(2, discountRuleResult.orderId());
                    statement.setInt(3, discountRuleResult.quantity());
                    setNullableTimestamp(statement, 4, discountRuleResult.batchExpiryDate());
                    statement.setBigDecimal(5, discountRuleResult.finalPrice());
                    statement.setBigDecimal(6, discountRuleResult.appliedDiscountPct());
                    statement.setString(7, discountRuleResult.discountBreakdown());
                });
    }

    public void updateDiscountRuleResult(DiscountRuleResult discountRuleResult) {
        jdbcOperations.update(
                """
                UPDATE discount_rule_results
                SET quantity = ?, batch_expiry_date = ?, final_price = ?, applied_discount_pct = ?,
                    discount_breakdown = ?, computed_at = CURRENT_TIMESTAMP
                WHERE order_line_id = ?
                """,
                statement -> {
                    statement.setInt(1, discountRuleResult.quantity());
                    setNullableTimestamp(statement, 2, discountRuleResult.batchExpiryDate());
                    statement.setBigDecimal(3, discountRuleResult.finalPrice());
                    statement.setBigDecimal(4, discountRuleResult.appliedDiscountPct());
                    statement.setString(5, discountRuleResult.discountBreakdown());
                    statement.setString(6, discountRuleResult.orderLineId());
                });
    }

    public void deleteDiscountRuleResult(String orderLineId) {
        jdbcOperations.update(
                "DELETE FROM discount_rule_results WHERE order_line_id = ?",
                statement -> statement.setString(1, orderLineId));
    }

    public void createPromotion(Promotion promotion) {
        jdbcOperations.update(
                """
                INSERT INTO promotions
                (promo_id, promo_name, coupon_code, discount_type, discount_value, start_date, end_date,
                 eligible_sku_ids, min_cart_value, max_uses, current_use_count, expired)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> bindPromotion(statement, promotion));
    }

    public Optional<Promotion> getPromotion(String promoId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM promotions WHERE promo_id = ?",
                this::mapPromotion,
                statement -> statement.setString(1, promoId));
    }

    public Optional<Promotion> getPromotionByCouponCode(String couponCode) {
        return jdbcOperations.queryOne(
                "SELECT * FROM promotions WHERE coupon_code = ?",
                this::mapPromotion,
                statement -> statement.setString(1, couponCode));
    }

    public List<Promotion> listPromotions() {
        return jdbcOperations.query(
                "SELECT * FROM promotions ORDER BY start_date DESC",
                this::mapPromotion);
    }

    public List<Promotion> listActivePromotions() {
        return jdbcOperations.query(
                """
                SELECT * FROM promotions
                WHERE start_date <= CURRENT_TIMESTAMP AND end_date > CURRENT_TIMESTAMP AND expired = FALSE
                ORDER BY start_date DESC
                """,
                this::mapPromotion);
    }

    public List<Promotion> listPromotionsBySku(String skuId) {
        return jdbcOperations.query(
                """
                SELECT p.*
                FROM promotions p
                JOIN promotion_eligible_skus pes ON p.promo_id = pes.promo_id
                WHERE pes.sku_id = ?
                ORDER BY p.start_date DESC
                """,
                this::mapPromotion,
                statement -> statement.setString(1, skuId));
    }

    public List<Promotion> listExpiredPromotions() {
        return jdbcOperations.query(
                """
                SELECT * FROM promotions
                WHERE end_date < CURRENT_TIMESTAMP OR expired = TRUE
                ORDER BY end_date DESC
                """,
                this::mapPromotion);
    }

    public void updatePromotion(Promotion promotion) {
        jdbcOperations.update(
                """
                UPDATE promotions
                SET promo_name = ?, coupon_code = ?, discount_type = ?, discount_value = ?, start_date = ?,
                    end_date = ?, eligible_sku_ids = ?, min_cart_value = ?, max_uses = ?, current_use_count = ?,
                    expired = ?
                WHERE promo_id = ?
                """,
                statement -> {
                    statement.setString(1, promotion.promoName());
                    statement.setString(2, promotion.couponCode());
                    statement.setString(3, promotion.discountType());
                    statement.setBigDecimal(4, promotion.discountValue());
                    statement.setTimestamp(5, Timestamp.valueOf(promotion.startDate()));
                    statement.setTimestamp(6, Timestamp.valueOf(promotion.endDate()));
                    statement.setString(7, promotion.eligibleSkuIds());
                    statement.setBigDecimal(8, promotion.minCartValue());
                    statement.setInt(9, promotion.maxUses());
                    statement.setInt(10, promotion.currentUseCount());
                    statement.setBoolean(11, promotion.expired());
                    statement.setString(12, promotion.promoId());
                });
    }

    public void updatePromotionUseCount(String promoId, int newCount) {
        jdbcOperations.update(
                "UPDATE promotions SET current_use_count = ? WHERE promo_id = ?",
                statement -> {
                    statement.setInt(1, newCount);
                    statement.setString(2, promoId);
                });
    }

    public void updatePromotionExpired(String promoId, boolean expired) {
        jdbcOperations.update(
                "UPDATE promotions SET expired = ? WHERE promo_id = ?",
                statement -> {
                    statement.setBoolean(1, expired);
                    statement.setString(2, promoId);
                });
    }

    public void deletePromotion(String promoId) {
        jdbcOperations.inTransaction(connection -> {
            deleteByKey(connection, "DELETE FROM promotion_eligible_skus WHERE promo_id = ?", promoId);
            deleteByKey(connection, "DELETE FROM promotions WHERE promo_id = ?", promoId);
        });
    }

    public void createBundlePromotion(BundlePromotion bundlePromotion) {
        jdbcOperations.update(
                """
                INSERT INTO bundle_promotions
                (promo_id, promo_name, discount_pct, start_date, end_date, expired)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, bundlePromotion.promoId());
                    statement.setString(2, bundlePromotion.promoName());
                    statement.setBigDecimal(3, bundlePromotion.discountPct());
                    statement.setDate(4, Date.valueOf(bundlePromotion.startDate()));
                    statement.setDate(5, Date.valueOf(bundlePromotion.endDate()));
                    statement.setBoolean(6, bundlePromotion.expired());
                });
    }

    public void createBundlePromotionSku(BundlePromotionSku bundlePromotionSku) {
        jdbcOperations.update(
                "INSERT INTO bundle_promotion_skus (promo_id, sku_id) VALUES (?, ?)",
                statement -> {
                    statement.setString(1, bundlePromotionSku.promoId());
                    statement.setString(2, bundlePromotionSku.skuId());
                });
    }

    public void deleteBundlePromotionSku(String promoId, String skuId) {
        jdbcOperations.update(
                "DELETE FROM bundle_promotion_skus WHERE promo_id = ? AND sku_id = ?",
                statement -> {
                    statement.setString(1, promoId);
                    statement.setString(2, skuId);
                });
    }

    public Optional<BundlePromotion> getBundlePromotion(String promoId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM bundle_promotions WHERE promo_id = ?",
                resultSet -> new BundlePromotion(
                        resultSet.getString("promo_id"),
                        resultSet.getString("promo_name"),
                        resultSet.getBigDecimal("discount_pct"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        resultSet.getBoolean("expired")),
                statement -> statement.setString(1, promoId));
    }

    public void deleteBundlePromotion(String promoId) {
        jdbcOperations.inTransaction(connection -> {
            deleteByKey(connection, "DELETE FROM bundle_promotion_skus WHERE promo_id = ?", promoId);
            deleteByKey(connection, "DELETE FROM bundle_promotions WHERE promo_id = ?", promoId);
        });
    }

    public void createDiscountPolicy(DiscountPolicy discountPolicy) {
        jdbcOperations.update(
                """
                INSERT INTO discount_policies
                (policy_id, policy_name, stacking_rule, priority_level, max_discount_cap_pct, perishability_days,
                 clearance_discount_pct, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, discountPolicy.policyId());
                    statement.setString(2, discountPolicy.policyName());
                    statement.setString(3, discountPolicy.stackingRule());
                    statement.setInt(4, discountPolicy.priorityLevel());
                    statement.setBigDecimal(5, discountPolicy.maxDiscountCapPct());
                    statement.setInt(6, discountPolicy.perishabilityDays());
                    statement.setBigDecimal(7, discountPolicy.clearanceDiscountPct());
                    statement.setBoolean(8, discountPolicy.active());
                });
    }

    public Optional<DiscountPolicy> getDiscountPolicy(String policyId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM discount_policies WHERE policy_id = ?",
                resultSet -> new DiscountPolicy(
                        resultSet.getString("policy_id"),
                        resultSet.getString("policy_name"),
                        resultSet.getString("stacking_rule"),
                        resultSet.getInt("priority_level"),
                        resultSet.getBigDecimal("max_discount_cap_pct"),
                        resultSet.getInt("perishability_days"),
                        resultSet.getBigDecimal("clearance_discount_pct"),
                        resultSet.getBoolean("is_active")),
                statement -> statement.setString(1, policyId));
    }

    public void deleteDiscountPolicy(String policyId) {
        jdbcOperations.update(
                "UPDATE discount_policies SET is_active = FALSE WHERE policy_id = ?",
                statement -> statement.setString(1, policyId));
    }

    public void createRebateProgram(RebateProgram rebateProgram) {
        jdbcOperations.update(
                """
                INSERT INTO rebate_programs
                (program_id, customer_id, sku_id, target_spend, accumulated_spend, rebate_pct)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, rebateProgram.programId());
                    statement.setString(2, rebateProgram.customerId());
                    statement.setString(3, rebateProgram.skuId());
                    statement.setBigDecimal(4, rebateProgram.targetSpend());
                    statement.setBigDecimal(5, rebateProgram.accumulatedSpend());
                    statement.setBigDecimal(6, rebateProgram.rebatePct());
                });
    }

    public Optional<RebateProgram> getRebateProgram(String programId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM rebate_programs WHERE program_id = ?",
                resultSet -> new RebateProgram(
                        resultSet.getString("program_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("sku_id"),
                        resultSet.getBigDecimal("target_spend"),
                        resultSet.getBigDecimal("accumulated_spend"),
                        resultSet.getBigDecimal("rebate_pct")),
                statement -> statement.setString(1, programId));
    }

    public List<RebateProgram> listRebateProgramsByCustomer(String customerId) {
        return jdbcOperations.query(
                "SELECT * FROM rebate_programs WHERE customer_id = ? ORDER BY program_id ASC",
                resultSet -> new RebateProgram(
                        resultSet.getString("program_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("sku_id"),
                        resultSet.getBigDecimal("target_spend"),
                        resultSet.getBigDecimal("accumulated_spend"),
                        resultSet.getBigDecimal("rebate_pct")),
                statement -> statement.setString(1, customerId));
    }

    public List<RebateProgram> listRebateProgramsBySku(String skuId) {
        return jdbcOperations.query(
                "SELECT * FROM rebate_programs WHERE sku_id = ? ORDER BY program_id ASC",
                resultSet -> new RebateProgram(
                        resultSet.getString("program_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("sku_id"),
                        resultSet.getBigDecimal("target_spend"),
                        resultSet.getBigDecimal("accumulated_spend"),
                        resultSet.getBigDecimal("rebate_pct")),
                statement -> statement.setString(1, skuId));
    }

    public void updateRebateAccumulatedSpend(String programId, BigDecimal newAmount) {
        jdbcOperations.update(
                "UPDATE rebate_programs SET accumulated_spend = ? WHERE program_id = ?",
                statement -> {
                    statement.setBigDecimal(1, newAmount);
                    statement.setString(2, programId);
                });
    }

    public void deleteRebateProgram(String programId) {
        jdbcOperations.update(
                "DELETE FROM rebate_programs WHERE program_id = ?",
                statement -> statement.setString(1, programId));
    }

    public void createVolumeDiscountSchedule(VolumeDiscountSchedule schedule) {
        jdbcOperations.update(
                "INSERT INTO volume_discount_schedules (schedule_id, sku_id) VALUES (?, ?)",
                statement -> {
                    statement.setString(1, schedule.scheduleId());
                    statement.setString(2, schedule.skuId());
                });
    }

    public Optional<VolumeDiscountSchedule> getVolumeDiscountSchedule(String scheduleId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM volume_discount_schedules WHERE schedule_id = ?",
                resultSet -> new VolumeDiscountSchedule(
                        resultSet.getString("schedule_id"),
                        resultSet.getString("sku_id")),
                statement -> statement.setString(1, scheduleId));
    }

    public List<VolumeDiscountSchedule> listVolumeDiscountSchedules() {
        return jdbcOperations.query(
                "SELECT * FROM volume_discount_schedules ORDER BY schedule_id ASC",
                resultSet -> new VolumeDiscountSchedule(
                        resultSet.getString("schedule_id"),
                        resultSet.getString("sku_id")));
    }

    public void createVolumeTierRule(VolumeTierRule tierRule) {
        jdbcOperations.update(
                """
                INSERT INTO volume_tier_rules (schedule_id, min_qty, max_qty, discount_pct)
                VALUES (?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, tierRule.scheduleId());
                    statement.setInt(2, tierRule.minQty());
                    statement.setInt(3, tierRule.maxQty());
                    statement.setBigDecimal(4, tierRule.discountPct());
                });
    }

    public List<VolumeTierRule> getVolumeTierRules(String scheduleId) {
        return jdbcOperations.query(
                "SELECT * FROM volume_tier_rules WHERE schedule_id = ? ORDER BY min_qty ASC",
                resultSet -> new VolumeTierRule(
                        resultSet.getLong("id"),
                        resultSet.getString("schedule_id"),
                        resultSet.getInt("min_qty"),
                        resultSet.getInt("max_qty"),
                        resultSet.getBigDecimal("discount_pct")),
                statement -> statement.setString(1, scheduleId));
    }

    public void deleteVolumeTierRule(long id) {
        jdbcOperations.update(
                "DELETE FROM volume_tier_rules WHERE id = ?",
                statement -> statement.setLong(1, id));
    }

    public void deleteVolumeDiscountSchedule(String scheduleId) {
        jdbcOperations.inTransaction(connection -> {
            deleteByKey(connection, "DELETE FROM volume_tier_rules WHERE schedule_id = ?", scheduleId);
            deleteByKey(connection, "DELETE FROM volume_discount_schedules WHERE schedule_id = ?", scheduleId);
        });
    }

    public void createCustomerTierCache(CustomerTierCache tierCache) {
        jdbcOperations.update(
                """
                INSERT INTO customer_tier_cache (customer_id, tier, evaluated_at)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                """,
                statement -> {
                    statement.setString(1, tierCache.customerId());
                    statement.setString(2, tierCache.tier());
                });
    }

    public Optional<CustomerTierCache> getCustomerTierCache(String customerId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM customer_tier_cache WHERE customer_id = ?",
                resultSet -> new CustomerTierCache(
                        resultSet.getString("customer_id"),
                        resultSet.getString("tier"),
                        resultSet.getTimestamp("evaluated_at").toInstant()),
                statement -> statement.setString(1, customerId));
    }

    public void updateCustomerTierCache(CustomerTierCache tierCache) {
        jdbcOperations.update(
                """
                UPDATE customer_tier_cache
                SET tier = ?, evaluated_at = CURRENT_TIMESTAMP
                WHERE customer_id = ?
                """,
                statement -> {
                    statement.setString(1, tierCache.tier());
                    statement.setString(2, tierCache.customerId());
                });
    }

    public void deleteCustomerTierCache(String customerId) {
        jdbcOperations.update(
                "DELETE FROM customer_tier_cache WHERE customer_id = ?",
                statement -> statement.setString(1, customerId));
    }

    public void createCustomerTierOverride(CustomerTierOverride tierOverride) {
        jdbcOperations.update(
                """
                INSERT INTO customer_tier_overrides (customer_id, override_tier, override_set_at)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                """,
                statement -> {
                    statement.setString(1, tierOverride.customerId());
                    statement.setString(2, tierOverride.overrideTier());
                });
    }

    public void updateCustomerTierOverride(CustomerTierOverride tierOverride) {
        jdbcOperations.update(
                """
                UPDATE customer_tier_overrides
                SET override_tier = ?, override_set_at = CURRENT_TIMESTAMP
                WHERE customer_id = ?
                """,
                statement -> {
                    statement.setString(1, tierOverride.overrideTier());
                    statement.setString(2, tierOverride.customerId());
                });
    }

    public void deleteCustomerTierOverride(String customerId) {
        jdbcOperations.update(
                "DELETE FROM customer_tier_overrides WHERE customer_id = ?",
                statement -> statement.setString(1, customerId));
    }

    public void createRegionalPricingMultiplier(RegionalPricingMultiplier multiplier) {
        jdbcOperations.update(
                "INSERT INTO regional_pricing_multipliers (region_code, multiplier) VALUES (?, ?)",
                statement -> {
                    statement.setString(1, multiplier.regionCode());
                    statement.setBigDecimal(2, multiplier.multiplier());
                });
    }

    public Optional<RegionalPricingMultiplier> getRegionalMultiplier(String regionCode) {
        return jdbcOperations.queryOne(
                "SELECT * FROM regional_pricing_multipliers WHERE region_code = ?",
                resultSet -> new RegionalPricingMultiplier(
                        resultSet.getString("region_code"),
                        resultSet.getBigDecimal("multiplier")),
                statement -> statement.setString(1, regionCode));
    }

    public void deleteRegionalPricingMultiplier(String regionCode) {
        jdbcOperations.update(
                "DELETE FROM regional_pricing_multipliers WHERE region_code = ?",
                statement -> statement.setString(1, regionCode));
    }

    public void createContractPricing(ContractPricing contractPricing) {
        jdbcOperations.update(
                """
                INSERT INTO contract_pricing
                (contract_id, contract_customer_id, contract_sku_id, negotiated_price, contract_start_date, contract_expiry_date, contract_status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, contractPricing.contractId());
                    statement.setString(2, contractPricing.contractCustomerId());
                    statement.setString(3, contractPricing.contractSkuId());
                    statement.setBigDecimal(4, contractPricing.negotiatedPrice());
                    statement.setTimestamp(5, Timestamp.valueOf(contractPricing.contractStartDate()));
                    statement.setTimestamp(6, Timestamp.valueOf(contractPricing.contractExpiryDate()));
                    statement.setString(7, contractPricing.contractStatus());
                });
    }

    public Optional<ContractPricing> getContractPricing(String contractId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM contract_pricing WHERE contract_id = ?",
                resultSet -> new ContractPricing(
                        resultSet.getString("contract_id"),
                        resultSet.getString("contract_customer_id"),
                        resultSet.getString("contract_sku_id"),
                        resultSet.getBigDecimal("negotiated_price"),
                        resultSet.getTimestamp("contract_start_date").toLocalDateTime(),
                        resultSet.getTimestamp("contract_expiry_date").toLocalDateTime(),
                        resultSet.getString("contract_status")),
                statement -> statement.setString(1, contractId));
    }

    public void deleteContractPricing(String contractId) {
        jdbcOperations.update(
                "DELETE FROM contract_pricing WHERE contract_id = ?",
                statement -> statement.setString(1, contractId));
    }

    public void createPriceApproval(PriceApproval priceApproval) {
        jdbcOperations.update(
                """
                INSERT INTO price_approvals
                (approval_id, request_type, requested_by, requested_discount_amt, justification_text,
                 approving_manager_id, approval_status, approval_timestamp, audit_log_flag, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                statement -> {
                    statement.setString(1, priceApproval.approvalId());
                    statement.setString(2, priceApproval.requestType());
                    statement.setString(3, priceApproval.requestedBy());
                    statement.setBigDecimal(4, priceApproval.requestedDiscountAmount());
                    statement.setString(5, priceApproval.justificationText());
                    statement.setString(6, priceApproval.approvingManagerId());
                    statement.setString(7, priceApproval.approvalStatus());
                    setNullableTimestamp(statement, 8, priceApproval.approvalTimestamp());
                    statement.setBoolean(9, priceApproval.auditLogFlag());
                });
    }

    public Optional<PriceApproval> getPriceApproval(String approvalId) {
        return jdbcOperations.queryOne(
                "SELECT * FROM price_approvals WHERE approval_id = ?",
                resultSet -> new PriceApproval(
                        resultSet.getString("approval_id"),
                        resultSet.getString("request_type"),
                        resultSet.getString("requested_by"),
                        resultSet.getBigDecimal("requested_discount_amt"),
                        resultSet.getString("justification_text"),
                        resultSet.getString("approving_manager_id"),
                        resultSet.getString("approval_status"),
                        getNullableDateTime(resultSet.getTimestamp("approval_timestamp")),
                        resultSet.getBoolean("audit_log_flag"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()),
                statement -> statement.setString(1, approvalId));
    }

    public List<PriceApproval> listPendingApprovals() {
        return listApprovalsByStatus("PENDING");
    }

    public List<PriceApproval> listApprovalsByStatus(String status) {
        return jdbcOperations.query(
                "SELECT * FROM price_approvals WHERE approval_status = ? ORDER BY created_at DESC",
                resultSet -> new PriceApproval(
                        resultSet.getString("approval_id"),
                        resultSet.getString("request_type"),
                        resultSet.getString("requested_by"),
                        resultSet.getBigDecimal("requested_discount_amt"),
                        resultSet.getString("justification_text"),
                        resultSet.getString("approving_manager_id"),
                        resultSet.getString("approval_status"),
                        getNullableDateTime(resultSet.getTimestamp("approval_timestamp")),
                        resultSet.getBoolean("audit_log_flag"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()),
                statement -> statement.setString(1, status));
    }

    public List<PriceApproval> listApprovalsByRequestedBy(String employeeId) {
        return jdbcOperations.query(
                "SELECT * FROM price_approvals WHERE requested_by = ? ORDER BY created_at DESC",
                resultSet -> new PriceApproval(
                        resultSet.getString("approval_id"),
                        resultSet.getString("request_type"),
                        resultSet.getString("requested_by"),
                        resultSet.getBigDecimal("requested_discount_amt"),
                        resultSet.getString("justification_text"),
                        resultSet.getString("approving_manager_id"),
                        resultSet.getString("approval_status"),
                        getNullableDateTime(resultSet.getTimestamp("approval_timestamp")),
                        resultSet.getBoolean("audit_log_flag"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()),
                statement -> statement.setString(1, employeeId));
    }

    public void updatePriceApprovalStatus(String approvalId, String newStatus) {
        jdbcOperations.update(
                """
                UPDATE price_approvals
                SET approval_status = ?, approval_timestamp = CURRENT_TIMESTAMP
                WHERE approval_id = ?
                """,
                statement -> {
                    statement.setString(1, newStatus);
                    statement.setString(2, approvalId);
                });
    }

    public void updatePriceApprovalManager(String approvalId, String managerId) {
        jdbcOperations.update(
                "UPDATE price_approvals SET approving_manager_id = ? WHERE approval_id = ?",
                statement -> {
                    statement.setString(1, managerId);
                    statement.setString(2, approvalId);
                });
    }

    public void deletePriceApproval(String approvalId) {
        jdbcOperations.update(
                """
                UPDATE price_approvals
                SET approval_status = 'CANCELLED', approval_timestamp = CURRENT_TIMESTAMP
                WHERE approval_id = ?
                """,
                statement -> statement.setString(1, approvalId));
    }

    private void bindPromotion(PreparedStatement statement, Promotion promotion) throws java.sql.SQLException {
        statement.setString(1, promotion.promoId());
        statement.setString(2, promotion.promoName());
        statement.setString(3, promotion.couponCode());
        statement.setString(4, promotion.discountType());
        statement.setBigDecimal(5, promotion.discountValue());
        statement.setTimestamp(6, Timestamp.valueOf(promotion.startDate()));
        statement.setTimestamp(7, Timestamp.valueOf(promotion.endDate()));
        statement.setString(8, promotion.eligibleSkuIds());
        statement.setBigDecimal(9, promotion.minCartValue());
        statement.setInt(10, promotion.maxUses());
        statement.setInt(11, promotion.currentUseCount());
        statement.setBoolean(12, promotion.expired());
    }

    private Promotion mapPromotion(java.sql.ResultSet resultSet) throws java.sql.SQLException {
        return new Promotion(
                resultSet.getString("promo_id"),
                resultSet.getString("promo_name"),
                resultSet.getString("coupon_code"),
                resultSet.getString("discount_type"),
                resultSet.getBigDecimal("discount_value"),
                resultSet.getTimestamp("start_date").toLocalDateTime(),
                resultSet.getTimestamp("end_date").toLocalDateTime(),
                resultSet.getString("eligible_sku_ids"),
                resultSet.getBigDecimal("min_cart_value"),
                resultSet.getInt("max_uses"),
                resultSet.getInt("current_use_count"),
                resultSet.getBoolean("expired"));
    }

    private PriceList mapPriceList(java.sql.ResultSet resultSet) throws java.sql.SQLException {
        return new PriceList(
                resultSet.getString("price_id"),
                resultSet.getString("sku_id"),
                resultSet.getString("region_code"),
                resultSet.getString("channel"),
                resultSet.getString("price_type"),
                resultSet.getBigDecimal("base_price"),
                resultSet.getBigDecimal("price_floor"),
                resultSet.getString("currency_code"),
                resultSet.getTimestamp("effective_from").toLocalDateTime(),
                resultSet.getTimestamp("effective_to").toLocalDateTime(),
                resultSet.getString("status"));
    }

    private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws java.sql.SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
            return;
        }
        statement.setInt(index, value);
    }

    private void setNullableTimestamp(PreparedStatement statement, int index, LocalDateTime value)
            throws java.sql.SQLException {
        if (value == null) {
            statement.setNull(index, Types.TIMESTAMP);
            return;
        }
        statement.setTimestamp(index, Timestamp.valueOf(value));
    }

    private LocalDateTime getNullableDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private Integer getNullableInteger(int value, boolean wasNull) {
        return wasNull ? null : value;
    }

    private void deleteByKey(Connection connection, String sql, String key) throws java.sql.SQLException {
        jdbcOperations.update(connection, sql, statement -> statement.setString(1, key));
    }
}
