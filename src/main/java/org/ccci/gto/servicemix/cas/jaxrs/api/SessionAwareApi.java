package org.ccci.gto.servicemix.cas.jaxrs.api;

import static org.ccci.gto.servicemix.cas.jaxrs.api.Constants.PARAM_SESSION;

import javax.ws.rs.core.UriInfo;

import org.ccci.gto.servicemix.cas.SessionManager;
import org.ccci.gto.servicemix.cas.model.Session;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SessionAwareApi {
    @Autowired
    private SessionManager sessionManager;

    /**
     * @return the sessionManager
     */
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    /**
     * @param sessionManager
     *            the sessionManager to set
     */
    public void setSessionManager(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    protected Session getSession(final UriInfo uri) {
        return sessionManager.getSession(uri.getPathParameters().getFirst(PARAM_SESSION));
    }
}
