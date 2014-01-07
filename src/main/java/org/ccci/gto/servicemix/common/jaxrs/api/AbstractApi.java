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

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.DestinationWithEndpoint;
import org.ccci.gto.servicemix.common.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractApi {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractApi.class);

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

    @Deprecated
    protected URI getBaseUri(final UriInfo uri) {
        return this.getBaseUri(null, uri);
    }

    protected URI getBaseUri(final MessageContext cxt, UriInfo uri) {
        return this.getBaseUri(cxt, uri, true);
    }

    private URI getBaseUri(final MessageContext cxt, UriInfo uri, final boolean defaultBase) {
        // short-circuit if we have a baseUri
        if (this.baseUri != null) {
            return this.baseUri;
        }

        // check for a publishedEndpointUrl
        if (cxt != null) {
            final Object d = cxt.get(Destination.class.getName());
            if (d instanceof DestinationWithEndpoint) {
                final EndpointInfo ei = ((DestinationWithEndpoint) d).getEndpointInfo();
                if (ei != null) {
                    final String endpointUrl = ei.getProperty("publishedEndpointUrl", String.class);
                    if (StringUtils.isNotBlank(endpointUrl)) {
                        try {
                            return new URI(endpointUrl);
                        } catch (final Exception e) {
                            LOG.error("error parsing publishedEndpointUrl", e);
                        }
                    }
                }
            }
        }

        // should we allow a default base url
        if (defaultBase) {
            // get a UriInfo object if we don't have one
            if (uri == null && cxt != null) {
                uri = cxt.getUriInfo();
            }

            if (uri != null) {
                return uri.getBaseUri();
            }
        }

        return null;
    }

    @Deprecated
    protected UriBuilder getBaseUriBuilder(final UriInfo uri) {
        return this.getBaseUriBuilder(null, uri);
    }

    protected UriBuilder getBaseUriBuilder(final MessageContext cxt, final UriInfo uri) {
        return UriBuilder.fromUri(this.getBaseUri(cxt, uri));
    }

    @Deprecated
    protected URI getRequestUri(final UriInfo uri) {
        return this.getRequestUri(null, uri);
    }

    protected URI getRequestUri(final MessageContext cxt, UriInfo uri) {
        // get a UriInfo object if we don't have one
        if (uri == null && cxt != null) {
            uri = cxt.getUriInfo();
        }

        if (uri != null) {
            // utilize a custom baseUri if requested
            final URI baseUri = this.getBaseUri(cxt, uri, false);
            if (baseUri != null) {
                return baseUri.resolve(uri.getBaseUri().relativize(uri.getRequestUri()));
            }

            // return the default request uri
            return uri.getRequestUri();
        }

        // the request uri could not be determined
        return null;
    }

    @Deprecated
    protected UriBuilder getRequestUriBuilder(final UriInfo uri) {
        return this.getRequestUriBuilder(null, uri);
    }

    protected UriBuilder getRequestUriBuilder(final MessageContext cxt, final UriInfo uri) {
        return UriBuilder.fromUri(this.getRequestUri(cxt, uri));
    }

    protected String getApiGroup() {
        return this.apiGroup;
    }

    protected String getAuthRealm() {
        return this.authRealm;
    }

    @Deprecated
    protected ResponseBuilder unauthorized(final UriInfo uri) {
        return this.unauthorized();
    }

    protected ResponseBuilder unauthorized() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(AUTH_PARAM_REALM, this.getAuthRealm());
        return ResponseUtils.unauthorized(AUTH_SCHEME_UNAUTHORIZED, params);
    }
}
