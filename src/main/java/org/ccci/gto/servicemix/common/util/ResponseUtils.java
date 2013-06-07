package org.ccci.gto.servicemix.common.util;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

public final class ResponseUtils {
    public static final String AUTH_SCHEME_UNAUTHORIZED = "Unauthorized";
    public static final String AUTH_SCHEME_CAS = "CAS";
    public static final String AUTH_PARAM_CASSERVER = "casUrl";
    public static final String AUTH_PARAM_CASSERVICE = "service";
    public static final String AUTH_PARAM_REALM = "realm";
    public static final String AUTH_PARAM_REALM_DEFAULT = "api";

    public static final Map<String, String> AUTH_PARAMS_DEFAULT;
    static {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(AUTH_PARAM_REALM, AUTH_PARAM_REALM_DEFAULT);
        AUTH_PARAMS_DEFAULT = Collections.unmodifiableMap(params);
    }

    private static final Pattern PATTERN_TOKEN = Pattern.compile("^[^\\x00-\\x20\\7F()<>@,;:/?={}\"\\[\\]\\\\]+$");
    private static final Pattern PATTERN_PARAM_VALUE = Pattern.compile("^[^\\x00-\\x1A\\7F()\"\\\\]+$");

    public static ResponseBuilder unauthorized() {
        return unauthorized(AUTH_SCHEME_UNAUTHORIZED);
    }

    public static ResponseBuilder unauthorizedCas(final URI casServer, final URI service) {
        final Map<String,String> params = new HashMap<String, String>();
        params.put(AUTH_PARAM_CASSERVER, casServer.toString());
        params.put(AUTH_PARAM_CASSERVICE, service.toString());
        return unauthorized(AUTH_SCHEME_CAS, params);
    }

    public static ResponseBuilder unauthorized(final String scheme) {
        return unauthorized(scheme, AUTH_PARAMS_DEFAULT);
    }

    public static ResponseBuilder unauthorized(final String scheme, final Map<String, String> params) {
        final ResponseBuilder response = Response.status(Status.UNAUTHORIZED);

        // was a valid scheme specified
        if (PATTERN_TOKEN.matcher(scheme).matches()) {
            final StringBuilder sb = new StringBuilder();

            // realm should be the first parameter
            // XXX: this is required for a parsing bug in android
            // (http://stackoverflow.com/questions/14550131)
            if (params.containsKey(AUTH_PARAM_REALM)) {
                final String value = params.get(AUTH_PARAM_REALM);
                if (PATTERN_PARAM_VALUE.matcher(value).matches()) {
                    sb.append(AUTH_PARAM_REALM).append("=\"").append(value).append("\"");
                }
            }

            // append the remaining params
            for (final Entry<String, String> entry : params.entrySet()) {
                final String param = entry.getKey();
                final String value = entry.getValue();

                // skip the realm parameter since it was already added
                if (AUTH_PARAM_REALM.equals(param)) {
                    continue;
                }

                // skip invalid params
                if (!PATTERN_TOKEN.matcher(param).matches() || !PATTERN_PARAM_VALUE.matcher(value).matches()) {
                    continue;
                }

                // use a separator if we have previous params
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                // append the current param
                sb.append(param).append("=\"").append(value).append("\"");
            }

            // attach the WWW-Authenticate header
            response.header(HttpHeaders.WWW_AUTHENTICATE, scheme + " " + sb.toString());
        }

        // return the response
        return response;
    }
}
