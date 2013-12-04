package org.ccci.gto.servicemix.common;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.ccci.gto.servicemix.common.model.Client;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
public class ClientManagerImpl implements ClientManager {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Client getClient(final String grouping, final long id) {
        return this.em.find(Client.class, new Client.PrimaryKey(grouping, id));
    }

    @Override
    public Client findClient(final String grouping, final String apiKey) {
        final TypedQuery<Client> query = this.em.createNamedQuery("Client.findByApiKey", Client.class);

        // bind parameters
        query.setParameter("grouping", grouping);
        query.setParameter("key", apiKey);

        // set limits for this query
        query.setFirstResult(0);
        query.setMaxResults(1);

        try {
            // return the first result
            return query.getSingleResult();
        } catch (final NoResultException e) {
            return null;
        }
    }
}
