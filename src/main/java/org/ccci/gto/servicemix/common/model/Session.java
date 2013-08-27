package org.ccci.gto.servicemix.common.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "Session.expireSessions", query = "DELETE FROM Session s WHERE s.expireTime < CURRENT_TIME")
public class Session {
    @Id
    @Column(length = 36, updatable = false)
    private String id;
    @Column(length = 36, updatable = false)
    private String guid;
    @Column(length = 20, updatable = false, nullable = false)
    private String grouping;

    private Date expireTime;

    public Session() {
    }

    public Session(final String grouping, final String id, final String guid) {
        this();
        this.id = id;
        this.guid = guid;
        this.grouping = grouping;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    public String getGrouping() {
        return this.grouping;
    }

    /**
     * @return the guid
     */
    public String getGuid() {
        return this.guid;
    }

    public boolean isExpired() {
        return this.expireTime == null || new Date().after(this.expireTime);
    }

    public void setExpireTime(final Date date) {
        this.expireTime = date;
    }
}
