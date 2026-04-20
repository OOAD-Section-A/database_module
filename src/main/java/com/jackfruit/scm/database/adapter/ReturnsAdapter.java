package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.ReturnsModels.ProductReturn;
import com.jackfruit.scm.database.model.ReturnsModels.ReturnGrowthStatistic;
import java.util.List;

public class ReturnsAdapter {

    private final SupplyChainDatabaseFacade facade;

    public ReturnsAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createProductReturn(ProductReturn productReturn) {
        facade.returns().createProductReturn(productReturn);
    }

    public void deleteProductReturn(String returnRequestId) {
        facade.returns().deleteProductReturn(returnRequestId);
    }

    public List<ProductReturn> listProductReturns() {
        return facade.returns().listProductReturns();
    }

    public void updateDefectDetails(String returnRequestId, String defectDetails) {
        facade.returns().updateDefectDetails(returnRequestId, defectDetails);
    }

    public void updateCustomerFeedback(String returnRequestId, String customerFeedback) {
        facade.returns().updateCustomerFeedback(returnRequestId, customerFeedback);
    }

    public void createReturnGrowthStatistic(ReturnGrowthStatistic statistic) {
        facade.returns().createReturnGrowthStatistic(statistic);
    }

    public void deleteReturnGrowthStatistic(String growthStatId) {
        facade.returns().deleteReturnGrowthStatistic(growthStatId);
    }

    public List<ReturnGrowthStatistic> listReturnGrowthStatistics() {
        return facade.returns().listReturnGrowthStatistics();
    }
}
