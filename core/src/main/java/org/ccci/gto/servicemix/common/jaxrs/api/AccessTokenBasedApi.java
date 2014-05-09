package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_ACCESS_TOKEN;

import com.google.common.base.CharMatcher;
import org.apache.cxf.jaxrs.ext.MessageContext;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AccessTokenBasedApi extends AbstractApi {
    private static final CharMatcher MATCHER_FORWARD_SLASH = CharMatcher.is('/');
    private static final Pattern PATTERN_AUTHORIZATION_BEARER = Pattern.compile("^Bearer (.*)$");

    protected final String getAccessToken(final MessageContext cxt, HttpHeaders headers, UriInfo uri) {
        if (cxt != null) {
            if (uri == null) {
                uri = cxt.getUriInfo();
            }
            if (headers == null) {
                headers = cxt.getHttpHeaders();
            }
        }

        // look up the access token in the following preference order:
        // 1. path parameter
        // 2. query parameter
        // 3. Authorization header
        String accessToken = null;
        if (uri != null) {
            accessToken = this.getAccessTokenFromPath(uri);

            if (accessToken == null) {
                accessToken = this.getAccessTokenFromQuery(uri);
            }
        }
        if (accessToken == null && headers != null) {
            accessToken = this.getAccessTokenFromAuthorizationHeader(headers);
        }
        return accessToken;
    }

    protected abstract String getAccessTokenPathParam();

    private String getAccessTokenFromPath(UriInfo uri) {
        final String accessToken = uri.getPathParameters().getFirst(getAccessTokenPathParam());
        return accessToken != null ? MATCHER_FORWARD_SLASH.trimFrom(accessToken) : null;
    }

    protected String getAccessTokenQueryParam() {
        return PARAM_ACCESS_TOKEN;
    }

    private String getAccessTokenFromQuery(UriInfo uri) {
        final String accessToken = uri.getQueryParameters().getFirst(getAccessTokenQueryParam());
        return accessToken != null ? accessToken : null;
    }

    private String getAccessTokenFromAuthorizationHeader(final HttpHeaders headers) {
        // check for Bearer Authorization header
        final List<String> values = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (values != null) {
            for (final String auth : values) {
                final Matcher matcher = PATTERN_AUTHORIZATION_BEARER.matcher(auth);
                if (matcher.matches()) {
                    return matcher.group(1);
                }
            }
        }

        return null;
    }
}
