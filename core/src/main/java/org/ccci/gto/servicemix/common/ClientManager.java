package org.ccci.gto.servicemix.common;

import org.ccci.gto.servicemix.common.model.Client;

public interface ClientManager {
    Client getClient(String grouping, long id);

    Client findClient(String grouping, String apiKey);
}
