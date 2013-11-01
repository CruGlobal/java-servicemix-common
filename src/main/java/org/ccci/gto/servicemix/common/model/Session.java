package org.ccci.gto.servicemix.common.model;

import static org.ccci.gto.servicemix.common.Constants.GUID_GUEST;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "Session.expireSessions", query = "DELETE FROM Session s WHERE s.expireTime < CURRENT_TIME")
public class Session {
    @EmbeddedId
    private SessionPrimaryKey key;

    @Column(length = 36, updatable = false, nullable = false)
    private String guid;

    private Date expireTime;

    public Session() {
        this(new SessionPrimaryKey(), null);
    }

    public Session(final String grouping, final String id, final String guid) {
        this(new SessionPrimaryKey(grouping, id), guid);
    }

    public Session(final SessionPrimaryKey key, final String guid) {
        this.key = key;
        this.guid = guid;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.key.getId();
    }

    public String getGrouping() {
        return this.key.getGrouping();
    }

    public SessionPrimaryKey getKey() {
        return this.key;
    }

    /**
     * @return the guid
     */
    public String getGuid() {
        return this.guid;
    }

    public boolean isGuest() {
        return GUID_GUEST.equals(this.guid);
    }

    public boolean isExpired() {
        return this.expireTime == null || new Date().after(this.expireTime);
    }

    public void setExpireTime(final Date date) {
        this.expireTime = date;
    }
}
