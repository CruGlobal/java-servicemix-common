package org.ccci.gto.servicemix.common.util;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

public final class ResponseUtils {
    private static final String AUTH_SCHEME_UNAUTHORIZED = "Unauthorized";
    private static final String AUTH_SCHEME_CAS = "CAS";

    public static ResponseBuilder unauthorized() {
        return unauthorized(AUTH_SCHEME_UNAUTHORIZED);
    }

    public static ResponseBuilder unauthorizedCas(final URI casServer, final URI service) {
        // XXX: I'm not a fan of assuming the URI are escaped properly, but I
        // could not find a header escaping class
        return unauthorized(AUTH_SCHEME_CAS + " casUrl=\"" + casServer + "\", service=\"" + service + "\"");
    }

    public static ResponseBuilder unauthorized(final String scheme) {
        return Response.status(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, scheme);
    }
}
