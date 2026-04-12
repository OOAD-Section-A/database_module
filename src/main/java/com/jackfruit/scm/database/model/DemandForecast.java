package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DemandForecast {

    private String forecastId;
    private String productId;
    private String forecastPeriod;
    private int predictedDemand;
    private BigDecimal confidenceScore;
    private LocalDateTime generatedAt;
    private String sourceEventReference;

    public DemandForecast() {
    }

    public DemandForecast(String forecastId, String productId, String forecastPeriod, int predictedDemand,
                          BigDecimal confidenceScore, LocalDateTime generatedAt, String sourceEventReference) {
        this.forecastId = forecastId;
        this.productId = productId;
        this.forecastPeriod = forecastPeriod;
        this.predictedDemand = predictedDemand;
        this.confidenceScore = confidenceScore;
        this.generatedAt = generatedAt;
        this.sourceEventReference = sourceEventReference;
    }

    public String getForecastId() {
        return forecastId;
    }

    public void setForecastId(String forecastId) {
        this.forecastId = forecastId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getForecastPeriod() {
        return forecastPeriod;
    }

    public void setForecastPeriod(String forecastPeriod) {
        this.forecastPeriod = forecastPeriod;
    }

    public int getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(int predictedDemand) {
        this.predictedDemand = predictedDemand;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getSourceEventReference() {
        return sourceEventReference;
    }

    public void setSourceEventReference(String sourceEventReference) {
        this.sourceEventReference = sourceEventReference;
    }
}
