package org.ccci.gto.servicemix.cas;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.ccci.gto.servicemix.cas.model.Session;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false)
public final class SessionManager {
    @PersistenceContext
    private EntityManager em;

    private int maxAge = 6;

    public Session createSession(final String guid) {
        final Session session = new Session(UUID.randomUUID().toString().replace("-", "").toLowerCase(), guid);
        session.setExpireTime(this.getExpireTime());
        this.em.persist(session);
        return session;
    }

    public Session getSession(final String sessionId) {
        final Session session = this.em.find(Session.class, sessionId);
        if (session != null && !session.isExpired()) {
            session.setExpireTime(this.getExpireTime());
            return session;
        }
        return null;
    }

    public void removeSession(final String sessionId) {
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
