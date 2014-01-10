package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_SESSION;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.ccci.gto.servicemix.common.SessionManager;
import org.ccci.gto.servicemix.common.model.Session;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SessionAwareApi extends AbstractApi {
    @Autowired
    private SessionManager sessionManager;

    /**
     * @return the sessionManager
     */
    protected SessionManager getSessionManager() {
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
        return sessionManager.getSession(this.getApiGroup(), uri.getPathParameters().getFirst(PARAM_SESSION));
    }

    @Deprecated
    public void setSessionGrouping(final String grouping) {
        this.setApiGroup(grouping);
    }

    @Deprecated
    protected String getSessionGrouping() {
        return this.getApiGroup();
    }

    @Deprecated
    protected final ResponseBuilder invalidSession(final UriInfo uri) {
        return this.unauthorized(uri);
    }
}
