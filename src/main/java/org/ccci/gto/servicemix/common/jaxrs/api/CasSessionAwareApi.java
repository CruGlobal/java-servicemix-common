package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_PARAM_CASSERVER;
import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_PARAM_CASSERVICE;
import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_PARAM_REALM;
import static org.ccci.gto.servicemix.common.util.ResponseUtils.AUTH_SCHEME_CAS;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.ccci.gto.servicemix.common.util.ResponseUtils;

public abstract class CasSessionAwareApi extends SessionAwareApi {
    private static final String CAS_SERVER_DEFAULT = "https://thekey.me/cas/";

    private String casBaseUri = CAS_SERVER_DEFAULT;

    public void setCasBaseUri(final String uri) {
        this.casBaseUri = uri != null ? uri : CAS_SERVER_DEFAULT;
    }

    protected URI getCasServiceUri(final UriInfo uri) {
        return this.getBaseUriBuilder(uri).path(AuthenticationApi.class).path(AuthenticationApi.class, "login").build();
    }

    @Override
    protected ResponseBuilder invalidSession(final UriInfo uri) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(AUTH_PARAM_REALM, this.getAuthRealm());
        params.put(AUTH_PARAM_CASSERVER, this.casBaseUri);
        params.put(AUTH_PARAM_CASSERVICE, this.getCasServiceUri(uri).toString());
        return ResponseUtils.unauthorized(AUTH_SCHEME_CAS, params);
    }
}
