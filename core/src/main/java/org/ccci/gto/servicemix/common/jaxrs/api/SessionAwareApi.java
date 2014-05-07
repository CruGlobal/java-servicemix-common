package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_SESSION;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.ccci.gto.servicemix.common.SessionManager;
import org.ccci.gto.servicemix.common.model.Session;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

public abstract class SessionAwareApi extends AccessTokenBasedApi {
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

    @Override
    protected final String getAccessTokenPathParam() {
        return PARAM_SESSION;
    }

    @Deprecated
    protected Session getSession(final UriInfo uri) {
        return this.getSession(null, uri);
    }

    protected Session getSession(final MessageContext cxt, final UriInfo uri) {
        return sessionManager.getSession(this.getApiGroup(), this.getAccessToken(cxt, null, uri));
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
