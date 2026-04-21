package com.jackfruit.scm.database.dao;

import com.jackfruit.scm.database.model.InventoryBatch;
import java.util.List;
import java.util.Optional;

public interface InventoryBatchDao {

    void save(InventoryBatch inventoryBatch);

    void update(InventoryBatch inventoryBatch);

    Optional<InventoryBatch> findById(String batchId);

    List<InventoryBatch> findAll();

    List<InventoryBatch> findByProductId(String productId);

    List<InventoryBatch> findByProductAndLocation(String productId, String locationId);
}
