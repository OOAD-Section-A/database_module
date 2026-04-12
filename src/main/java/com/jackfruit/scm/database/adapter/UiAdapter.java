package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.UiModels.UiNotification;
import com.jackfruit.scm.database.model.UiModels.UiUser;

public class UiAdapter {

    private final SupplyChainDatabaseFacade facade;

    public UiAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createUser(UiUser user) {
        facade.ui().createUser(user);
    }

    public void createNotification(UiNotification notification) {
        facade.ui().createNotification(notification);
    }
}
