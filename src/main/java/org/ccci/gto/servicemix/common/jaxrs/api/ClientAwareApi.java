package org.ccci.gto.servicemix.common.jaxrs.api;

import static org.ccci.gto.servicemix.common.jaxrs.api.Constants.PARAM_API_KEY;

import javax.ws.rs.core.UriInfo;

import org.ccci.gto.servicemix.common.ClientManager;
import org.ccci.gto.servicemix.common.model.Client;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ClientAwareApi extends AbstractApi {
    @Autowired
    private ClientManager clientManager;

    public void setClientManager(final ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    protected Client getClient(final UriInfo uri) {
        return this.clientManager.findClient(this.getApiGroup(), uri.getPathParameters().getFirst(PARAM_API_KEY));
    }
}
