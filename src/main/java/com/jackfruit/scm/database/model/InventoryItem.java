package com.jackfruit.scm.database.model;

public class InventoryItem {

    private String productId;
    private String locationId;
    private Integer totalQuantity;
    private Integer reservedQuantity;
    private Character abcCategory;
    private Integer reorderThreshold;
    private Integer safetyStockLevel;
    private Integer version;

    public InventoryItem() {
    }

    public InventoryItem(String productId, String locationId, Integer totalQuantity,
                        Integer reservedQuantity, Character abcCategory, Integer reorderThreshold,
                        Integer safetyStockLevel, Integer version) {
        this.productId = productId;
        this.locationId = locationId;
        this.totalQuantity = totalQuantity;
        this.reservedQuantity = reservedQuantity;
        this.abcCategory = abcCategory;
        this.reorderThreshold = reorderThreshold;
        this.safetyStockLevel = safetyStockLevel;
        this.version = version;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Character getAbcCategory() {
        return abcCategory;
    }

    public void setAbcCategory(Character abcCategory) {
        this.abcCategory = abcCategory;
    }

    public Integer getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(Integer reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public Integer getSafetyStockLevel() {
        return safetyStockLevel;
    }

    public void setSafetyStockLevel(Integer safetyStockLevel) {
        this.safetyStockLevel = safetyStockLevel;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
