package org.ccci.gto.servicemix.common;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.ccci.gto.servicemix.common.model.Session;
import org.ccci.gto.servicemix.common.model.Session.PrimaryKey;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
public final class SessionManagerImpl implements SessionManager {
    private static final String DEFAULT_GROUPING = "";

    @PersistenceContext
    private EntityManager em;

    private int maxAge = 6;

    @Override
    public Session createSession(final String guid) {
        return this.createSession(DEFAULT_GROUPING, guid);
    }

    @Override
    public Session createSession(final String grouping, final String guid) {
        final Session session = new Session(grouping, UUID.randomUUID().toString().replace("-", "").toLowerCase(), guid);
        session.setExpireTime(this.getExpireTime());
        this.em.persist(session);
        return session;
    }

    @Override
    public Session getSession(final String sessionId) {
        return this.getSession(DEFAULT_GROUPING, sessionId);
    }

    @Override
    public Session getSession(final String grouping, final String sessionId) {
        final Session session = this.em.find(Session.class, new PrimaryKey(grouping, sessionId));
        if (session != null && !session.isExpired()) {
            session.setExpireTime(this.getExpireTime());
            return session;
        }
        return null;
    }

    @Override
    public void removeSession(final String sessionId) {
        this.removeSession(DEFAULT_GROUPING, sessionId);
    }

    @Override
    public void removeSession(final String grouping, final String sessionId) {
        final Session session = this.em.find(Session.class, sessionId);
        if (session != null) {
            this.em.remove(session);
        }
    }

    public void expireSessions() {
        this.em.createNamedQuery("Session.expireSessions").executeUpdate();
    }

    private Date getExpireTime() {
        // calculate the expire time
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, maxAge);
        return cal.getTime();
    }
}
