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
}
