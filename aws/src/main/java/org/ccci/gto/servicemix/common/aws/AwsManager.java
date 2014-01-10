package org.ccci.gto.servicemix.common.aws;

import org.ccci.gto.servicemix.common.aws.model.SnsSubscription;

public interface AwsManager {
    SnsSubscription createSnsSubscription(String grouping, String arn, String topicArn);

    SnsSubscription getSnsSubscription(String grouping, String arn);

    void removeSnsSubscription(String grouping, String arn);
}
