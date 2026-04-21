package com.jackfruit.scm.database.dao;

import com.jackfruit.scm.database.model.StockTransaction;
import java.util.List;
import java.util.Optional;

public interface StockTransactionDao {

    void save(StockTransaction stockTransaction);

    Optional<StockTransaction> findById(String transactionId);

    List<StockTransaction> findAll();

    List<StockTransaction> findByProductId(String productId);

    List<StockTransaction> findByProductAndLocation(String productId, String locationId);

    List<StockTransaction> findByReferenceId(String referenceId);
}
