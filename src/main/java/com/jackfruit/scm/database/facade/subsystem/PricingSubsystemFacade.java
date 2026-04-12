package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.PricingModels.Promotion;
import com.jackfruit.scm.database.model.PricingModels.TierDefinition;
import com.jackfruit.scm.database.service.JdbcOperations;
import com.jackfruit.scm.database.service.PricingService;
import java.sql.Timestamp;
import java.util.List;

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

    public List<PriceList> listPrices() {
        return pricingService.getAllPrices();
    }

    public void createTierDefinition(TierDefinition tierDefinition) {
        jdbcOperations.update(
                "INSERT INTO tier_definitions (tier_name, min_spend_threshold, default_discount_pct) VALUES (?, ?, ?)",
                statement -> {
                    statement.setString(1, tierDefinition.tierName());
                    statement.setBigDecimal(2, tierDefinition.minSpendThreshold());
                    statement.setBigDecimal(3, tierDefinition.defaultDiscountPct());
                });
    }

    public List<TierDefinition> listTierDefinitions() {
        return jdbcOperations.query(
                "SELECT tier_id, tier_name, min_spend_threshold, default_discount_pct FROM tier_definitions",
                resultSet -> new TierDefinition(
                        resultSet.getInt("tier_id"),
                        resultSet.getString("tier_name"),
                        resultSet.getBigDecimal("min_spend_threshold"),
                        resultSet.getBigDecimal("default_discount_pct")));
    }

    public void createPromotion(Promotion promotion) {
        jdbcOperations.update(
                """
                INSERT INTO promotions
                (promo_id, promo_name, coupon_code, discount_type, discount_value, start_date, end_date,
                 eligible_sku_ids, min_cart_value, max_uses, current_use_count)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
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
                });
    }

    public List<Promotion> listPromotions() {
        return jdbcOperations.query(
                "SELECT * FROM promotions",
                resultSet -> new Promotion(
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
                        resultSet.getInt("current_use_count")));
    }
}
