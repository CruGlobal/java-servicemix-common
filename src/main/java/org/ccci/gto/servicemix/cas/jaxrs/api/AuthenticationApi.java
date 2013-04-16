package org.ccci.gto.servicemix.cas.jaxrs.api;

import static org.ccci.gto.servicemix.cas.jaxrs.api.Constants.PARAM_SESSION;
import static org.ccci.gto.servicemix.cas.jaxrs.api.Constants.PARAM_TICKET;
import static org.ccci.gto.servicemix.cas.jaxrs.api.Constants.PATH_SESSION;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.ccci.gto.servicemix.cas.model.Session;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("auth")
public class AuthenticationApi extends SessionAwareApi {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationApi.class);

    private static final String ATTR_GUID = "guid";

    private TicketValidator validator;

    private String serviceUri;

    /**
     * @return the serviceUrl
     */
    @GET
    @Path("service")
    public String getServiceUri(@Context final UriInfo uri) {
        // use any configured serviceUri
        if (!StringUtils.isBlank(this.serviceUri)) {
            return this.serviceUri;
        }

        // default to the url for the web service
        return uri.getBaseUri().toString();
    }

    @POST
    @Path("login")
    public Response login(@Context final UriInfo uri, @FormParam(PARAM_TICKET) final String ticket) {
        if (!StringUtils.isBlank(ticket)) {
            try {
                final Assertion assertion = this.validator.validate(ticket, this.getServiceUri(uri));
                final String guid = (String) assertion.getPrincipal().getAttributes().get(ATTR_GUID);

                if (!StringUtils.isBlank(guid)) {
                    final Session session = this.getSessionManager().createSession(guid);
                    return Response.ok(session.getId()).build();
                }
            } catch (final TicketValidationException e) {
                LOG.debug("exception validating ticket", e);
            }
        }
        return Response.status(Status.UNAUTHORIZED).build();
    }

    @DELETE
    @Path("session/" + PATH_SESSION)
    public Response logout(@PathParam(PARAM_SESSION) final String sessionId) {
        this.getSessionManager().removeSession(sessionId);
        return Response.ok().build();
    }

    /**
     * @param serviceUrl
     *            the serviceUrl to set
     */
    public void setServiceUri(final String serviceUri) {
        this.serviceUri = serviceUri;
    }

    /**
     * @param validator
     *            the validator to set
     */
    public void setValidator(final TicketValidator validator) {
        this.validator = validator;
    }
}
