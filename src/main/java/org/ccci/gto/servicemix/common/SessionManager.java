package org.ccci.gto.servicemix.common;

import org.ccci.gto.servicemix.common.model.Session;

public interface SessionManager {
    @Deprecated
    Session createSession(String guid);

    @Deprecated
    Session getSession(String sessionId);

    @Deprecated
    void removeSession(String sessionId);

    Session createSession(String grouping, String guid);

    Session getSession(String grouping, String sessionId);

    void removeSession(String grouping, String sessionId);
}
