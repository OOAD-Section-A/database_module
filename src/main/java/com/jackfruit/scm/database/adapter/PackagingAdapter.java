package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.PackagingModels.PackagingJob;
import com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord;
import com.jackfruit.scm.database.model.PackagingModels.RepairRequest;

public class PackagingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public PackagingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createPackagingJob(PackagingJob job) {
        facade.packaging().createPackagingJob(job);
    }

    public void createRepairRequest(RepairRequest request) {
        facade.packaging().createRepairRequest(request);
    }

    public void createReceiptRecord(ReceiptRecord record) {
        facade.packaging().createReceiptRecord(record);
    }
}
