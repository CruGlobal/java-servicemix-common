package org.ccci.gto.servicemix.common.jaxrs.api;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public abstract class AbstractApi {
    private URI baseUri = null;

    public void setBaseUri(final String baseUri) {
        this.baseUri = URI.create(baseUri);
    }

    public void setBaseUri(final URI baseUri) {
        this.baseUri = baseUri;
    }

    protected UriBuilder getBaseUri(final UriInfo uri) {
        if (this.baseUri == null) {
            return uri.getBaseUriBuilder();
        } else {
            return UriBuilder.fromUri(this.baseUri);
        }
    }

    protected UriBuilder getRequestUri(final UriInfo uri) {
        if (this.baseUri == null) {
            return uri.getRequestUriBuilder();
        } else {
            return UriBuilder.fromUri(this.baseUri.resolve(uri.getBaseUri().relativize(uri.getRequestUri())));
        }
    }
}
