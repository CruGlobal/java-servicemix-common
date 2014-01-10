package org.ccci.gto.servicemix.common.jaxrs.api;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.ccci.gto.servicemix.common.Constants.GUID_GUEST;
import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_GUEST;
import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_SESSION;
import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_TICKET;
import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PATH_SESSION;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.ccci.gto.servicemix.common.model.Session;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path("auth")
public class AuthenticationApi extends CasSessionAwareApi {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationApi.class);

    private static final String ATTR_GUID = "ssoGuid";

    @Autowired
    private TicketValidator validator;

    private boolean guestAccessEnabled = false;

    public final void setGuestAccessEnabled(final boolean guestAccessEnabled) {
        this.guestAccessEnabled = guestAccessEnabled;
    }

    /**
     * @return the serviceUri
     */
    @GET
    @Path("service")
    @Produces(TEXT_PLAIN)
    public String getServiceUri(@Context final MessageContext cxt, @Context final UriInfo uri) {
        return this.getCasServiceUri(cxt, uri).toString();
    }

    @POST
    @Path("login")
    @Produces(TEXT_PLAIN)
    public Response login(@Context final MessageContext cxt, @Context final UriInfo uri,
            @FormParam(PARAM_TICKET) final String ticket, @FormParam(PARAM_GUEST) final boolean guest) {
        if (CommonUtils.isNotBlank(ticket)) {
            try {
                final Assertion assertion = this.validator.validate(ticket, this.getCasServiceUri(cxt, uri).toString());
                final String guid = (String) assertion.getPrincipal().getAttributes().get(ATTR_GUID);

                if (CommonUtils.isNotBlank(guid)) {
                    final Session session = this.getSessionManager().createSession(this.getApiGroup(), guid);
                    return Response.ok(session.getId()).build();
                }
            } catch (final TicketValidationException e) {
                LOG.debug("exception validating ticket", e);
            }
        } else if (this.guestAccessEnabled && guest) {
            final Session session = this.getSessionManager().createSession(this.getApiGroup(), GUID_GUEST);
            return Response.ok(session.getId()).build();
        }

        return this.unauthorized(cxt, uri).build();
    }

    @DELETE
    @Path("session/" + PATH_SESSION)
    public Response logout(@PathParam(PARAM_SESSION) final String sessionId) {
        this.getSessionManager().removeSession(this.getApiGroup(), sessionId);
        return Response.ok().build();
    }

    /**
     * @param validator
     *            The CAS TicketValidator to use when validating tickets
     */
    public void setValidator(final TicketValidator validator) {
        this.validator = validator;
    }
}
