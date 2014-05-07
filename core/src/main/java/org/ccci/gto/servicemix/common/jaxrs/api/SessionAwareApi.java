package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_SESSION;

import com.google.common.base.CharMatcher;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.ccci.gto.servicemix.common.SessionManager;
import org.ccci.gto.servicemix.common.model.Session;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SessionAwareApi extends AbstractApi {
    private static final CharMatcher MATCHER_SLASHES = CharMatcher.is('/');
    private static final Pattern PATTERN_AUTH_BEARER = Pattern.compile("^Bearer (.*)$");

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

    protected String getSessionIdFromPath(final UriInfo uri) {
        final String sessionId = uri.getPathParameters().getFirst(PARAM_SESSION);
        return sessionId != null ? MATCHER_SLASHES.trimFrom(sessionId) : null;
    }

    protected String getSessionIdFromAuthorizationHeader(final HttpHeaders headers) {
        // check for Bearer Authorization header
        final List<String> values = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (values != null) {
            for (final String auth : values) {
                final Matcher matcher = PATTERN_AUTH_BEARER.matcher(auth);
                if (matcher.matches()) {
                    return matcher.group(1);
                }
            }
        }

        return null;
    }

    protected String getSessionId(final MessageContext cxt, HttpHeaders headers, UriInfo uri) {
        if (cxt != null) {
            if (uri == null) {
                uri = cxt.getUriInfo();
            }
            if (headers == null) {
                headers = cxt.getHttpHeaders();
            }
        }

        // look up the sessionId
        String sessionId = null;
        if (uri != null) {
            sessionId = this.getSessionIdFromPath(uri);
        }
        if (sessionId == null && headers != null) {
            sessionId = this.getSessionIdFromAuthorizationHeader(headers);
        }
        return sessionId;
    }

    @Deprecated
    protected Session getSession(final UriInfo uri) {
        return this.getSession(null, uri);
    }

    protected Session getSession(final MessageContext cxt, final UriInfo uri) {
        return sessionManager.getSession(this.getApiGroup(), this.getSessionId(cxt, null, uri));
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
