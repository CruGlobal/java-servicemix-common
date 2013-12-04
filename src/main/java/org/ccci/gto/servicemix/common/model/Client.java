package org.ccci.gto.servicemix.common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import org.apache.commons.lang.StringUtils;

@Entity
@NamedQuery(name = "Client.findByApiKey", query = "SELECT c FROM Client c WHERE c.key.grouping = :grouping AND c.apiKey = :key")
public class Client {
    @EmbeddedId
    private PrimaryKey key;

    private String name;

    @Column(length = 36, unique = true, nullable = false)
    private String apiKey;

    private String description;

    public Client() {
        this(new PrimaryKey());
    }

    public Client(final String grouping, final long id) {
        this(new PrimaryKey(grouping, id));
    }

    public Client(final PrimaryKey key) {
        this.key = key;
    }

    public final String getGrouping() {
        return this.key.getGrouping();
    }

    public final long getId() {
        return this.key.getId();
    }

    public final PrimaryKey getKey() {
        return this.key;
    }

    public final String getApiKey() {
        return this.apiKey;
    }

    public final void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final void setDescription(final String description) {
        this.description = description;
    }

    @Embeddable
    public static class PrimaryKey implements Serializable {
        private static final long serialVersionUID = 8173375396366973730L;

        @Column(length = 20, updatable = false, nullable = false)
        private String grouping = null;
        @Column(updatable = false, nullable = false)
        private long id = 0;

        public PrimaryKey() {
        }

        public PrimaryKey(final String grouping, final long id) {
            this.grouping = grouping;
            this.id = id;
        }

        public String getGrouping() {
            return this.grouping;
        }

        public long getId() {
            return this.id;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = (hash * 31) + (this.grouping != null ? this.grouping.hashCode() : 0);
            hash = (hash * 31) + Long.valueOf(this.id).hashCode();
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof PrimaryKey)) {
                return false;
            }
            final PrimaryKey key2 = (PrimaryKey) obj;
            return StringUtils.equals(this.grouping, key2.grouping) && this.id == key2.id;
        }
    }
}
