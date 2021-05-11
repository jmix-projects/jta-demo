package com.company.jtatest.jta;

import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

public class AtomikosServerPlatform extends ServerPlatformBase {

    public AtomikosServerPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    @Override
    public Class getExternalTransactionControllerClass() {
        return AtomikosJtaTransactionController.class;
    }

}
