package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_PARAM_REALM;
import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_PARAM_REALM_DEFAULT;
import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_SCHEME_UNAUTHORIZED;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.ccci.gto.servicemix.common.util.ResponseUtils;

public abstract class AbstractApi {
    private static final String DEFAULT_API_GROUP = "";

    private URI baseUri = null;

    private String apiGroup = DEFAULT_API_GROUP;

    private String authRealm = AUTH_PARAM_REALM_DEFAULT;

    public void setBaseUri(final String baseUri) {
        this.baseUri = URI.create(baseUri);
    }

    public void setBaseUri(final URI baseUri) {
        this.baseUri = baseUri;
    }

    public void setApiGroup(final String group) {
        this.apiGroup = group != null ? group : DEFAULT_API_GROUP;
    }

    public void setAuthRealm(final String authRealm) {
        this.authRealm = authRealm != null ? authRealm : AUTH_PARAM_REALM_DEFAULT;
    }

    protected URI getBaseUri(final UriInfo uri) {
        if (this.baseUri != null) {
            return this.baseUri;
        }

        return uri.getBaseUri();
    }

    protected UriBuilder getBaseUriBuilder(final UriInfo uri) {
        if (this.baseUri != null) {
            return UriBuilder.fromUri(this.baseUri);
        }

        return uri.getBaseUriBuilder();
    }

    protected URI getRequestUri(final UriInfo uri) {
        if (this.baseUri != null) {
            this.baseUri.resolve(uri.getBaseUri().relativize(uri.getRequestUri()));
        }

        return uri.getRequestUri();
    }

    protected UriBuilder getRequestUriBuilder(final UriInfo uri) {
        if (this.baseUri != null) {
            return UriBuilder.fromUri(this.getRequestUri(uri));
        }

        return uri.getRequestUriBuilder();
    }

    protected String getApiGroup() {
        return this.apiGroup;
    }

    protected String getAuthRealm() {
        return this.authRealm;
    }

    protected ResponseBuilder unauthorized(final UriInfo uri) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(AUTH_PARAM_REALM, this.getAuthRealm());
        return ResponseUtils.unauthorized(AUTH_SCHEME_UNAUTHORIZED, params);
    }
}
