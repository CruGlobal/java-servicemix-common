package org.ccci.gto.servicemix.common.model;

import static org.ccci.gto.servicemix.common.Constants.GUID_GUEST;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import org.apache.commons.lang.StringUtils;

@Entity
@NamedQuery(name = "Session.expireSessions", query = "DELETE FROM Session s WHERE s.expireTime < CURRENT_TIME")
public class Session {
    @EmbeddedId
    private PrimaryKey key;

    @Column(length = 36, updatable = false, nullable = false)
    private String guid;

    private Date expireTime;

    public Session() {
        this(new PrimaryKey(), null);
    }

    public Session(final String grouping, final String id, final String guid) {
        this(new PrimaryKey(grouping, id), guid);
    }

    public Session(final PrimaryKey key, final String guid) {
        this.key = key;
        this.guid = guid;
    }

    public PrimaryKey getKey() {
        return this.key;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.key.id;
    }

    public String getGrouping() {
        return this.key.grouping;
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

    @Embeddable
    public static class PrimaryKey implements Serializable {
        private static final long serialVersionUID = 1228973296513056583L;

        @Column(length = 20, updatable = false, nullable = false)
        private String grouping = null;
        @Column(length = 36, updatable = false, unique = true)
        private String id = null;

        public PrimaryKey() {
        }

        public PrimaryKey(final String grouping, final String id) {
            this.grouping = grouping;
            this.id = id;
        }

        public String getGrouping() {
            return this.grouping;
        }

        public String getId() {
            return this.id;
        }

        @Override
        public int hashCode() {
            int hash = 0;
            hash = (hash * 31) + (this.grouping != null ? this.grouping.hashCode() : 0);
            hash = (hash * 31) + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof PrimaryKey)) {
                return false;
            }
            final PrimaryKey key2 = (PrimaryKey) obj;
            return StringUtils.equals(this.grouping, key2.grouping) && StringUtils.equals(this.id, key2.id);
        }
    }
}
