package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.DemandForecast;
import com.jackfruit.scm.database.model.DemandForecastingModels.ForecastPerformanceMetric;
import com.jackfruit.scm.database.model.DemandForecastingModels.HolidayCalendar;
import com.jackfruit.scm.database.model.DemandForecastingModels.InventorySupply;
import com.jackfruit.scm.database.model.DemandForecastingModels.ProductLifecycleStage;
import com.jackfruit.scm.database.model.DemandForecastingModels.ProductMetadata;
import com.jackfruit.scm.database.model.DemandForecastingModels.PromotionalCalendar;
import com.jackfruit.scm.database.model.DemandForecastingModels.SalesRecord;
import com.jackfruit.scm.database.model.ForecastTimeseries;
import java.util.List;

public class DemandForecastingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public DemandForecastingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createForecast(DemandForecast forecast) {
        facade.demandForecasting().createForecast(forecast);
    }

    public void deleteForecast(String forecastId) {
        facade.demandForecasting().deleteForecast(forecastId);
    }

    public void createSalesRecord(SalesRecord salesRecord) {
        facade.demandForecasting().createSalesRecord(salesRecord);
    }

    public void deleteSalesRecord(String saleId) {
        facade.demandForecasting().deleteSalesRecord(saleId);
    }

    public void createHolidayCalendar(HolidayCalendar holidayCalendar) {
        facade.demandForecasting().createHolidayCalendar(holidayCalendar);
    }

    public void deleteHolidayCalendar(String holidayId) {
        facade.demandForecasting().deleteHolidayCalendar(holidayId);
    }

    public void createPromotionalCalendar(PromotionalCalendar promotionalCalendar) {
        facade.demandForecasting().createPromotionalCalendar(promotionalCalendar);
    }

    public void deletePromotionalCalendar(String promoCalendarId) {
        facade.demandForecasting().deletePromotionalCalendar(promoCalendarId);
    }

    public void createProductMetadata(ProductMetadata productMetadata) {
        facade.demandForecasting().createProductMetadata(productMetadata);
    }

    public void deleteProductMetadata(String productId) {
        facade.demandForecasting().deleteProductMetadata(productId);
    }

    public void createProductLifecycleStage(ProductLifecycleStage stage) {
        facade.demandForecasting().createProductLifecycleStage(stage);
    }

    public void deleteProductLifecycleStage(String lifecycleId) {
        facade.demandForecasting().deleteProductLifecycleStage(lifecycleId);
    }

    public void createInventorySupply(InventorySupply inventorySupply) {
        facade.demandForecasting().createInventorySupply(inventorySupply);
    }

    public void deleteInventorySupply(String productId) {
        facade.demandForecasting().deleteInventorySupply(productId);
    }

    public void createForecastPerformanceMetric(ForecastPerformanceMetric metric) {
        facade.demandForecasting().createForecastPerformanceMetric(metric);
    }

    public void deleteForecastPerformanceMetric(String evalId) {
        facade.demandForecasting().deleteForecastPerformanceMetric(evalId);
    }

    public void createForecastTimeseries(ForecastTimeseries timeseries) {
        facade.demandForecasting().createForecastTimeseries(timeseries);
    }

    public void deleteForecastTimeseries(String timeseriesId) {
        facade.demandForecasting().deleteForecastTimeseries(timeseriesId);
    }

    public void createBatchForecastTimeseries(List<ForecastTimeseries> timeseriesList) {
        facade.demandForecasting().createBatchForecastTimeseries(timeseriesList);
    }
}
