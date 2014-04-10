package org.ccci.gto.servicemix.common.jaxrs.api;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.ccci.gto.servicemix.common.Constants.GUID_GUEST;
import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_GUEST;
import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_SESSION;
import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_TICKET;
import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PATH_SESSION;
import static org.ccci.gto.servicemix.common.util.RequestUtils.defaultAccept;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.ccci.gto.servicemix.common.jaxb.model.JaxbSession;
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
    @Produces({ APPLICATION_JSON })
    public Response login(@Context final MessageContext cxt, @Context final UriInfo uri,
            final MultivaluedMap<String, String> form) {
        // handle legacy login requests
        if (defaultAccept(cxt, null)) {
            LOG.error("Default login request!!!! a client is still issuing a login request without an accept header");
            return this.loginPlainText(cxt, uri, form);
        }

        final Session session = this.loginInternal(cxt, uri, form);
        if (session != null) {
            return Response.ok(new JaxbSession(session)).build();
        } else {
            return this.unauthorized(cxt, uri).build();
        }
    }

    @POST
    @Path("login")
    @Produces(TEXT_PLAIN)
    public Response loginPlainText(@Context final MessageContext cxt, @Context final UriInfo uri,
            final MultivaluedMap<String, String> form) {
        final Session session = this.loginInternal(cxt, uri, form);
        if (session != null) {
            return Response.ok(session.getId()).type(TEXT_PLAIN).build();
        } else {
            return this.unauthorized(cxt, uri).build();
        }
    }

    private Session loginInternal(final MessageContext cxt, final UriInfo uri, final MultivaluedMap<String, String> form) {
        final String ticket = form.getFirst(PARAM_TICKET);
        final boolean guest = Boolean.parseBoolean(form.getFirst(PARAM_GUEST));

        if (CommonUtils.isNotBlank(ticket)) {
            try {
                final Assertion assertion = this.validator.validate(ticket, this.getCasServiceUri(cxt, uri).toString());
                final String guid = (String) assertion.getPrincipal().getAttributes().get(ATTR_GUID);

                if (CommonUtils.isNotBlank(guid)) {
                    return this.getSessionManager().createSession(this.getApiGroup(), guid);
                }
            } catch (final TicketValidationException e) {
                LOG.debug("exception validating ticket", e);
            }
        } else if (this.guestAccessEnabled && guest) {
            return this.getSessionManager().createSession(this.getApiGroup(), GUID_GUEST);
        }

        return null;
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
