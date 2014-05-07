package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_API_KEY;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.ccci.gto.servicemix.common.ClientManager;
import org.ccci.gto.servicemix.common.model.Client;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.UriInfo;

public abstract class ClientAwareApi extends AccessTokenBasedApi {
    @Autowired
    private ClientManager clientManager;

    public void setClientManager(final ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    protected final String getAccessTokenPathParam() {
        return PARAM_API_KEY;
    }

    @Deprecated
    protected Client getClient(final UriInfo uri) {
        return this.getClient(null, uri);
    }

    protected Client getClient(final MessageContext cxt, final UriInfo uri) {
        return this.clientManager.findClient(this.getApiGroup(), this.getAccessToken(cxt, null, uri));
    }
}
