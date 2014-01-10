package org.ccci.gto.servicemix.common.aws.model;

import static org.ccci.gto.servicemix.common.aws.Constants.SNS_MESSAGE_MESSAGE;
import static org.ccci.gto.servicemix.common.aws.Constants.SNS_MESSAGE_SUBJECT;
import static org.ccci.gto.servicemix.common.aws.Constants.SNS_MESSAGE_TOPIC_ARN;

import com.amazonaws.util.json.JSONObject;

public class SnsNotification {
    private final SnsSubscription subscription;

    private final JSONObject msg;

    public SnsNotification(final SnsSubscription subscription, final JSONObject msg) {
        this.subscription = subscription;
        this.msg = msg;
    }

    public final SnsSubscription getSubscription() {
        return this.subscription;
    }

    public final String getTopicArn() {
        return this.msg != null ? this.msg.optString(SNS_MESSAGE_TOPIC_ARN, null) : null;
    }

    public final String getSubject() {
        return this.msg != null ? this.msg.optString(SNS_MESSAGE_SUBJECT, null) : null;
    }

    public final String getMessage() {
        return this.msg != null ? this.msg.optString(SNS_MESSAGE_MESSAGE, null) : null;
    }
}
