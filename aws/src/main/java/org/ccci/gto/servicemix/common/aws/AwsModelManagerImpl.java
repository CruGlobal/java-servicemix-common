package org.ccci.gto.servicemix.common.aws;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.ccci.gto.servicemix.common.aws.model.SnsSubscription;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class AwsModelManagerImpl implements AwsModelManager {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public SnsSubscription createSnsSubscription(final String grouping, final String arn, final String topicArn) {
        final SnsSubscription subscription = new SnsSubscription(grouping, arn);
        subscription.setTopicArn(topicArn);
        this.em.persist(subscription);
        return subscription;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public SnsSubscription getSnsSubscription(final String grouping, final String arn) {
        return this.em.find(SnsSubscription.class, new SnsSubscription.PrimaryKey(grouping, arn));
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void removeSnsSubscription(final String grouping, final String arn) {
        final SnsSubscription subscription = this.em.find(SnsSubscription.class, new SnsSubscription.PrimaryKey(
                grouping, arn));
        if (subscription != null) {
            this.em.remove(subscription);
        }
    }
}
