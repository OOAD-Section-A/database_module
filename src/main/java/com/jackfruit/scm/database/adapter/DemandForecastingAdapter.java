package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.DemandForecast;
import com.jackfruit.scm.database.model.DemandForecastingModels.ForecastPerformanceMetric;
import com.jackfruit.scm.database.model.DemandForecastingModels.SalesRecord;

public class DemandForecastingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public DemandForecastingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createForecast(DemandForecast forecast) {
        facade.demandForecasting().createForecast(forecast);
    }

    public void createSalesRecord(SalesRecord salesRecord) {
        facade.demandForecasting().createSalesRecord(salesRecord);
    }

    public void createForecastPerformanceMetric(ForecastPerformanceMetric metric) {
        facade.demandForecasting().createForecastPerformanceMetric(metric);
    }
}
