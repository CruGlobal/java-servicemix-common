package org.ccci.gto.servicemix.common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.StringUtils;

@Embeddable
public class SessionPrimaryKey implements Serializable {
    private static final long serialVersionUID = -3051449039964841426L;

    @Column(length = 20, updatable = false, nullable = false)
    private String grouping = null;
    @Column(length = 36, updatable = false)
    private String id = null;

    public SessionPrimaryKey() {
    }

    public SessionPrimaryKey(final String grouping, final String id) {
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
        int hash = super.hashCode();
        hash = (hash * 31) + (this.grouping != null ? this.grouping.hashCode() : 0);
        hash = (hash * 31) + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof SessionPrimaryKey)) {
            return false;
        }
        final SessionPrimaryKey key2 = (SessionPrimaryKey) obj;
        return StringUtils.equals(this.grouping, key2.grouping) && StringUtils.equals(this.id, key2.id);
    }
}
