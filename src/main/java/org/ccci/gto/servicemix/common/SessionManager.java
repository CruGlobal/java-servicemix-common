package org.ccci.gto.servicemix.common;

import org.ccci.gto.servicemix.common.model.Session;

public interface SessionManager {
    Session createSession(String guid);

    Session getSession(String sessionId);

    void removeSession(String sessionId);
}
