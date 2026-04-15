package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.UiModels.UiAuditLog;
import com.jackfruit.scm.database.model.UiModels.UiNotification;
import com.jackfruit.scm.database.model.UiModels.UiPanelState;
import com.jackfruit.scm.database.model.UiModels.UiSession;
import com.jackfruit.scm.database.model.UiModels.UiUser;

public class UiAdapter {

    private final SupplyChainDatabaseFacade facade;

    public UiAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createUser(UiUser user) {
        facade.ui().createUser(user);
    }

    public void createSession(UiSession session) {
        facade.ui().createSession(session);
    }

    public void createNotification(UiNotification notification) {
        facade.ui().createNotification(notification);
    }

    public void createAuditLog(UiAuditLog auditLog) {
        facade.ui().createAuditLog(auditLog);
    }

    public void createPanelState(UiPanelState panelState) {
        facade.ui().createPanelState(panelState);
    }
}
