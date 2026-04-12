package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ReportingModels {

    private ReportingModels() {
    }

    public record InventoryStockReportRow(String productId, String binId, String zoneId,
                                          String warehouseId, int currentStockLevel,
                                          LocalDateTime lastUpdated) {
    }

    public record PriceDiscountReportRow(String productId, BigDecimal productPrice, String regionCode,
                                         String channel, String currencyCode, String status) {
    }

    public record ExceptionReportRow(String exceptionId, String exceptionType, String severityLevel,
                                     LocalDateTime timestamp, String requestedBy,
                                     String justificationText) {
    }
}
