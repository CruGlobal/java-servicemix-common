package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_SESSION;
import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_PARAM_REALM;
import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_PARAM_REALM_DEFAULT;
import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_SCHEME_UNAUTHORIZED;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.ccci.gto.servicemix.common.SessionManager;
import org.ccci.gto.servicemix.common.model.Session;
import org.ccci.gto.servicemix.common.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SessionAwareApi extends AbstractApi {
    private static final String DEFAULT_SESSION_GROUP = "";

    @Autowired
    private SessionManager sessionManager;

    private String sessionGrouping = DEFAULT_SESSION_GROUP;

    private String authRealm = AUTH_PARAM_REALM_DEFAULT;

    /**
     * @return the sessionManager
     */
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    public void setAuthRealm(final String authRealm) {
        this.authRealm = authRealm != null ? authRealm : AUTH_PARAM_REALM_DEFAULT;
    }

    protected String getAuthRealm() {
        return this.authRealm;
    }

    public void setSessionGrouping(final String grouping) {
        this.sessionGrouping = grouping != null ? grouping : DEFAULT_SESSION_GROUP;
    }

    protected String getSessionGrouping() {
        return this.sessionGrouping;
    }

    /**
     * @param sessionManager
     *            the sessionManager to set
     */
    public void setSessionManager(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    protected Session getSession(final UriInfo uri) {
        return sessionManager.getSession(this.getSessionGrouping(), uri.getPathParameters().getFirst(PARAM_SESSION));
    }

    protected ResponseBuilder invalidSession(final UriInfo uri) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(AUTH_PARAM_REALM, this.getAuthRealm());
        return ResponseUtils.unauthorized(AUTH_SCHEME_UNAUTHORIZED, params);
    }
}
