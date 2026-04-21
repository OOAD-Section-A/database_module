package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.InventoryBatch;
import com.jackfruit.scm.database.model.InventoryItem;
import com.jackfruit.scm.database.model.InventoryModels.DeadStock;
import com.jackfruit.scm.database.model.InventoryModels.ExpiryTracking;
import com.jackfruit.scm.database.model.InventoryModels.Product;
import com.jackfruit.scm.database.model.InventoryModels.ProductBatch;
import com.jackfruit.scm.database.model.InventoryModels.ReorderManagement;
import com.jackfruit.scm.database.model.InventoryModels.StockAdjustment;
import com.jackfruit.scm.database.model.InventoryModels.StockFreeze;
import com.jackfruit.scm.database.model.InventoryModels.StockLevel;
import com.jackfruit.scm.database.model.InventoryModels.StockReservation;
import com.jackfruit.scm.database.model.InventoryModels.StockValuation;
import com.jackfruit.scm.database.model.StockTransaction;
import com.jackfruit.scm.database.model.Warehouse;
import java.util.List;

public class InventoryAdapter {

    private final SupplyChainDatabaseFacade facade;

    public InventoryAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void syncWarehouse(Warehouse warehouse) {
        facade.warehouse().registerWarehouse(warehouse);
    }

    public void createProduct(Product product) {
        facade.inventory().createProduct(product);
    }

    public void deleteProduct(String productId) {
        facade.inventory().deleteProduct(productId);
    }

    public void createStockLevel(StockLevel stockLevel) {
        facade.inventory().createStockLevel(stockLevel);
    }

    public void deleteStockLevel(String stockLevelId) {
        facade.inventory().deleteStockLevel(stockLevelId);
    }

    public void updateStockLevel(StockLevel stockLevel) {
        facade.inventory().updateStockLevel(stockLevel);
    }

    public List<Product> listProducts() {
        return facade.inventory().listProducts();
    }

    public void createProductBatch(ProductBatch productBatch) {
        facade.inventory().createProductBatch(productBatch);
    }

    public void deleteProductBatch(String batchId) {
        facade.inventory().deleteProductBatch(batchId);
    }

    public List<ProductBatch> listProductBatches() {
        return facade.inventory().listProductBatches();
    }

    public void createExpiryTracking(ExpiryTracking expiryTracking) {
        facade.inventory().createExpiryTracking(expiryTracking);
    }

    public void deleteExpiryTracking(String expiryId) {
        facade.inventory().deleteExpiryTracking(expiryId);
    }

    public List<ExpiryTracking> listExpiryTracking() {
        return facade.inventory().listExpiryTracking();
    }

    public List<StockLevel> listStockLevels() {
        return facade.inventory().listStockLevels();
    }

    public void createStockAdjustment(StockAdjustment stockAdjustment) {
        facade.inventory().createStockAdjustment(stockAdjustment);
    }

    public void deleteStockAdjustment(String adjustmentId) {
        facade.inventory().deleteStockAdjustment(adjustmentId);
    }

    public List<StockAdjustment> listStockAdjustments() {
        return facade.inventory().listStockAdjustments();
    }

    public void createReorderManagement(ReorderManagement reorderManagement) {
        facade.inventory().createReorderManagement(reorderManagement);
    }

    public void deleteReorderManagement(String reorderId) {
        facade.inventory().deleteReorderManagement(reorderId);
    }

    public List<ReorderManagement> listReorderManagement() {
        return facade.inventory().listReorderManagement();
    }

    public void createStockReservation(StockReservation stockReservation) {
        facade.inventory().createStockReservation(stockReservation);
    }

    public void deleteStockReservation(String reservationId) {
        facade.inventory().deleteStockReservation(reservationId);
    }

    public List<StockReservation> listStockReservations() {
        return facade.inventory().listStockReservations();
    }

    public void createStockFreeze(StockFreeze stockFreeze) {
        facade.inventory().createStockFreeze(stockFreeze);
    }

    public void deleteStockFreeze(String freezeId) {
        facade.inventory().deleteStockFreeze(freezeId);
    }

    public List<StockFreeze> listStockFreeze() {
        return facade.inventory().listStockFreeze();
    }

    public void createDeadStock(DeadStock deadStock) {
        facade.inventory().createDeadStock(deadStock);
    }

    public void deleteDeadStock(String deadStockId) {
        facade.inventory().deleteDeadStock(deadStockId);
    }

    public List<DeadStock> listDeadStock() {
        return facade.inventory().listDeadStock();
    }

    public void createStockValuation(StockValuation stockValuation) {
        facade.inventory().createStockValuation(stockValuation);
    }

    public void deleteStockValuation(String valuationId) {
        facade.inventory().deleteStockValuation(valuationId);
    }

    public List<StockValuation> listStockValuation() {
        return facade.inventory().listStockValuation();
    }

    // New Inventory Item Operations
    public void createInventoryItem(InventoryItem inventoryItem) {
        facade.inventory().createInventoryItem(inventoryItem);
    }

    public void updateInventoryItem(InventoryItem inventoryItem) {
        facade.inventory().updateInventoryItem(inventoryItem);
    }

    public InventoryItem getInventoryItem(String productId, String locationId) {
        return facade.inventory().getInventoryItem(productId, locationId);
    }

    public List<InventoryItem> listInventoryItems() {
        return facade.inventory().listInventoryItems();
    }

    public List<InventoryItem> findInventoryItemsByProduct(String productId) {
        return facade.inventory().findInventoryItemsByProduct(productId);
    }

    public List<InventoryItem> findInventoryItemsByLocation(String locationId) {
        return facade.inventory().findInventoryItemsByLocation(locationId);
    }

    // New Inventory Batch Operations
    public void createInventoryBatch(InventoryBatch inventoryBatch) {
        facade.inventory().createInventoryBatch(inventoryBatch);
    }

    public void updateInventoryBatch(InventoryBatch inventoryBatch) {
        facade.inventory().updateInventoryBatch(inventoryBatch);
    }

    public InventoryBatch getInventoryBatch(String batchId) {
        return facade.inventory().getInventoryBatch(batchId);
    }

    public List<InventoryBatch> listInventoryBatches() {
        return facade.inventory().listInventoryBatches();
    }

    public List<InventoryBatch> findInventoryBatchesByProduct(String productId) {
        return facade.inventory().findInventoryBatchesByProduct(productId);
    }

    public List<InventoryBatch> findInventoryBatchesByProductAndLocation(String productId, String locationId) {
        return facade.inventory().findInventoryBatchesByProductAndLocation(productId, locationId);
    }

    // Stock Transaction Audit Trail Operations
    public void recordStockTransaction(StockTransaction stockTransaction) {
        facade.inventory().recordStockTransaction(stockTransaction);
    }

    public StockTransaction getStockTransaction(String transactionId) {
        return facade.inventory().getStockTransaction(transactionId);
    }

    public List<StockTransaction> listStockTransactions() {
        return facade.inventory().listStockTransactions();
    }

    public List<StockTransaction> findStockTransactionsByProduct(String productId) {
        return facade.inventory().findStockTransactionsByProduct(productId);
    }

    public List<StockTransaction> findStockTransactionsByProductAndLocation(String productId, String locationId) {
        return facade.inventory().findStockTransactionsByProductAndLocation(productId, locationId);
    }

    public List<StockTransaction> findStockTransactionsByReference(String referenceId) {
        return facade.inventory().findStockTransactionsByReference(referenceId);
    }
}
