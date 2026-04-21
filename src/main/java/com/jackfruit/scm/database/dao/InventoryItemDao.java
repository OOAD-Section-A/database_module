package com.jackfruit.scm.database.dao;

import com.jackfruit.scm.database.model.InventoryItem;
import java.util.List;
import java.util.Optional;

public interface InventoryItemDao {

    void save(InventoryItem inventoryItem);

    void update(InventoryItem inventoryItem);

    Optional<InventoryItem> findById(String productId, String locationId);

    List<InventoryItem> findAll();

    List<InventoryItem> findByProductId(String productId);

    List<InventoryItem> findByLocationId(String locationId);
}
