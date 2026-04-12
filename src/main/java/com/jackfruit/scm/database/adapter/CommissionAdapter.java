package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.CommissionModels.Agent;
import com.jackfruit.scm.database.model.CommissionModels.CommissionHistory;
import com.jackfruit.scm.database.model.CommissionModels.CommissionSale;

public class CommissionAdapter {

    private final SupplyChainDatabaseFacade facade;

    public CommissionAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createAgent(Agent agent) {
        facade.commissions().createAgent(agent);
    }

    public void createSale(CommissionSale sale) {
        facade.commissions().createCommissionSale(sale);
    }

    public void createCommissionHistory(CommissionHistory history) {
        facade.commissions().createCommissionHistory(history);
    }
}
