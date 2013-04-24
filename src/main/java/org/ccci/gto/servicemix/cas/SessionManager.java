package org.ccci.gto.servicemix.cas;

import org.ccci.gto.servicemix.cas.model.Session;

public interface SessionManager {
    Session createSession(String guid);

    Session getSession(String sessionId);

    void removeSession(String sessionId);
}
