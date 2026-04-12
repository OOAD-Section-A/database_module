package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.PricingModels.Promotion;
import com.jackfruit.scm.database.model.PricingModels.TierDefinition;

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

    public void createPromotion(Promotion promotion) {
        facade.pricing().createPromotion(promotion);
    }
}
