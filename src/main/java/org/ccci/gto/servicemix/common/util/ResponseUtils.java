package org.ccci.gto.servicemix.common.util;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

public final class ResponseUtils {
    private static final String DEFAULT_AUTH_SCHEME = "Unauthorized";

    public static ResponseBuilder unauthorized() {
        return unauthorized(DEFAULT_AUTH_SCHEME);
    }

    public static ResponseBuilder unauthorized(final String scheme) {
        return Response.status(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, scheme);
    }
}
