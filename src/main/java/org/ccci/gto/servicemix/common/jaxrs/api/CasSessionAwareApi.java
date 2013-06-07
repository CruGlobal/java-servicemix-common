package org.ccci.gto.servicemix.common.jaxrs.api;

import java.net.URI;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.ccci.gto.servicemix.common.util.ResponseUtils;

public abstract class CasSessionAwareApi extends SessionAwareApi {
    private static final URI CAS_SERVER_DEFAULT = URI.create("https://thekey.me/cas/");

    private URI casBaseUri;

    public void setCasBaseUri(final String uri) {
        this.casBaseUri = URI.create(uri);
    }

    protected URI getCasBaseUri() {
        if (this.casBaseUri != null) {
            return this.casBaseUri;
        }

        return CAS_SERVER_DEFAULT;
    }

    protected URI getCasServiceUri(final UriInfo uri) {
        return this.getBaseUriBuilder(uri).path(AuthenticationApi.class).path(AuthenticationApi.class, "login").build();
    }

    @Override
    protected ResponseBuilder invalidSession(final UriInfo uri) {
        return ResponseUtils.unauthorizedCas(this.getCasBaseUri(), this.getCasServiceUri(uri));
    }
}
