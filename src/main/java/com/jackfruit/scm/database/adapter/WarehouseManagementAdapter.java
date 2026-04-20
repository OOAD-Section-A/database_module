package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.Warehouse;
import com.jackfruit.scm.database.model.WarehouseModels.Bin;
import com.jackfruit.scm.database.model.WarehouseModels.CycleCount;
import com.jackfruit.scm.database.model.WarehouseModels.GoodsReceipt;
import com.jackfruit.scm.database.model.WarehouseModels.PickTask;
import com.jackfruit.scm.database.model.WarehouseModels.StagingDispatch;
import com.jackfruit.scm.database.model.WarehouseModels.StockRecord;
import com.jackfruit.scm.database.model.WarehouseModels.StockMovement;
import com.jackfruit.scm.database.model.WarehouseModels.WarehouseReturn;
import com.jackfruit.scm.database.model.WarehouseModels.WarehouseZone;
import com.jackfruit.scm.database.model.WarehouseModels.WmsPickWave;
import com.jackfruit.scm.database.model.WarehouseModels.WmsStorageUnitLpn;
import com.jackfruit.scm.database.model.WarehouseModels.WmsTaskQueueItem;
import java.util.List;

public class WarehouseManagementAdapter {

    private final SupplyChainDatabaseFacade facade;

    public WarehouseManagementAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void registerWarehouse(Warehouse warehouse) {
        facade.warehouse().registerWarehouse(warehouse);
    }

    public void deleteWarehouse(String warehouseId) {
        facade.warehouse().deleteWarehouse(warehouseId);
    }

    public void createZone(WarehouseZone zone) {
        facade.warehouse().createZone(zone);
    }

    public void deleteZone(String zoneId) {
        facade.warehouse().deleteZone(zoneId);
    }

    public void createBin(Bin bin) {
        facade.warehouse().createBin(bin);
    }

    public void deleteBin(String binId) {
        facade.warehouse().deleteBin(binId);
    }

    public void createGoodsReceipt(GoodsReceipt goodsReceipt) {
        facade.warehouse().createGoodsReceipt(goodsReceipt);
    }

    public void deleteGoodsReceipt(String goodsReceiptId) {
        facade.warehouse().deleteGoodsReceipt(goodsReceiptId);
    }

    public void createStockRecord(StockRecord stockRecord) {
        facade.warehouse().createStockRecord(stockRecord);
    }

    public void deleteStockRecord(String stockId) {
        facade.warehouse().deleteStockRecord(stockId);
    }

    public void createStockMovement(StockMovement stockMovement) {
        facade.warehouse().createStockMovement(stockMovement);
    }

    public void deleteStockMovement(String movementId) {
        facade.warehouse().deleteStockMovement(movementId);
    }

    public void createPickTask(PickTask pickTask) {
        facade.warehouse().createPickTask(pickTask);
    }

    public void deletePickTask(String pickTaskId) {
        facade.warehouse().deletePickTask(pickTaskId);
    }

    public void createStagingDispatch(StagingDispatch stagingDispatch) {
        facade.warehouse().createStagingDispatch(stagingDispatch);
    }

    public void deleteStagingDispatch(String stagingId) {
        facade.warehouse().deleteStagingDispatch(stagingId);
    }

    public void createWarehouseReturn(WarehouseReturn warehouseReturn) {
        facade.warehouse().createWarehouseReturn(warehouseReturn);
    }

    public void deleteWarehouseReturn(String returnId) {
        facade.warehouse().deleteWarehouseReturn(returnId);
    }

    public void createCycleCount(CycleCount cycleCount) {
        facade.warehouse().createCycleCount(cycleCount);
    }

    public void deleteCycleCount(String cycleCountId) {
        facade.warehouse().deleteCycleCount(cycleCountId);
    }

    public void createStorageUnitLpn(WmsStorageUnitLpn storageUnit) {
        facade.warehouse().createStorageUnitLpn(storageUnit);
    }

    public void updateStorageUnitLpn(WmsStorageUnitLpn storageUnit) {
        facade.warehouse().updateStorageUnitLpn(storageUnit);
    }

    public void deleteStorageUnitLpn(String lpnId) {
        facade.warehouse().deleteStorageUnitLpn(lpnId);
    }

    public List<WmsStorageUnitLpn> listStorageUnitLpns() {
        return facade.warehouse().listStorageUnitLpns();
    }

    public void createPickWave(WmsPickWave pickWave) {
        facade.warehouse().createPickWave(pickWave);
    }

    public void updatePickWave(WmsPickWave pickWave) {
        facade.warehouse().updatePickWave(pickWave);
    }

    public void cancelPickWave(String waveId) {
        facade.warehouse().cancelPickWave(waveId);
    }

    public List<WmsPickWave> listPickWaves() {
        return facade.warehouse().listPickWaves();
    }

    public void createTaskQueueItem(WmsTaskQueueItem taskQueueItem) {
        facade.warehouse().createTaskQueueItem(taskQueueItem);
    }

    public void updateTaskQueueItem(WmsTaskQueueItem taskQueueItem) {
        facade.warehouse().updateTaskQueueItem(taskQueueItem);
    }

    public void deleteTaskQueueItem(String taskId) {
        facade.warehouse().deleteTaskQueueItem(taskId);
    }

    public List<WmsTaskQueueItem> listTaskQueueItems() {
        return facade.warehouse().listTaskQueueItems();
    }
}
