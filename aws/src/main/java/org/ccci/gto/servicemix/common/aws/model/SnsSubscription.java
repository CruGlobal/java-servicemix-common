package org.ccci.gto.servicemix.common.aws.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

@Entity
@Table(name = "AmazonSnsSubscriptions")
public class SnsSubscription {
    @EmbeddedId
    private PrimaryKey key;

    @Column()
    private String topicArn;

    public SnsSubscription() {
        this(new PrimaryKey(null, null));
    }

    public SnsSubscription(final String grouping, final String arn) {
        this(new PrimaryKey(grouping, arn));
    }

    public SnsSubscription(final PrimaryKey key) {
        this.key = key;
    }

    public final PrimaryKey getKey() {
        return this.key;
    }

    public final String getGrouping() {
        return this.key.grouping;
    }

    public final String getArn() {
        return this.key.arn;
    }

    public final String getTopicArn() {
        return this.topicArn;
    }

    public final void setTopicArn(final String arn) {
        this.topicArn = arn;
    }

    @Embeddable
    public static class PrimaryKey implements Serializable {
        private static final long serialVersionUID = 5422463548936990831L;

        @Column(length = 20, updatable = false, nullable = false)
        private String grouping = null;
        @Column(updatable = false, nullable = false, unique = true)
        private String arn = null;

        public PrimaryKey() {
            this(null, null);
        }

        public PrimaryKey(final String grouping, final String arn) {
            this.grouping = grouping;
            this.arn = arn;
        }

        @Override
        public int hashCode() {
            int hash = 0;
            hash = (hash * 31) + (this.grouping != null ? this.grouping.hashCode() : 0);
            hash = (hash * 31) + (this.arn != null ? this.arn.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof PrimaryKey)) {
                return false;
            }
            final PrimaryKey key2 = (PrimaryKey) obj;
            return StringUtils.equals(this.grouping, key2.grouping) && StringUtils.equals(this.arn, key2.arn);
        }
    }
}
