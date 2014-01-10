package org.ccci.gto.servicemix.common.aws.jaxrs.api;

import static org.ccci.gto.servicemix.common.aws.Constants.HEADER_AWS_SNS_SUBSCRIPTION_ARN;
import static org.ccci.gto.servicemix.common.aws.Constants.SNS_MESSAGE_SIGNING_URL;
import static org.ccci.gto.servicemix.common.aws.Constants.SNS_MESSAGE_TOKEN;
import static org.ccci.gto.servicemix.common.aws.Constants.SNS_MESSAGE_TOPIC_ARN;
import static org.ccci.gto.servicemix.common.aws.Constants.SNS_MESSAGE_TYPE;

import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.ccci.gto.servicemix.common.aws.AwsManager;
import org.ccci.gto.servicemix.common.aws.model.SnsSubscription;
import org.ccci.gto.servicemix.common.jaxrs.api.AbstractApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;
import com.amazonaws.services.sns.util.SignatureChecker;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

@Path("aws/sns")
public class AwsSnsApi extends AbstractApi {
    private static final Logger LOG = LoggerFactory.getLogger(AwsSnsApi.class);

    @Autowired
    private AmazonSNS sns;

    @Autowired
    private AwsManager manager;

    private final SignatureChecker snsVerify = new SignatureChecker();

    private static final Set<String> validDomains = new HashSet<>();
    static {
        for (final Region region : RegionUtils.getRegionsForService("sns")) {
            validDomains.add(region.getServiceEndpoint("sns"));
        }
    }

    @POST
    public Response snsCallback(@Context final HttpHeaders headers, final String raw) {
        // extract data from request
        final MultivaluedMap<String, String> rawHeaders = headers.getRequestHeaders();
        final JSONObject msg;
        try {
            msg = new JSONObject(raw);
        } catch (final JSONException e) {
            return Response.status(Status.BAD_REQUEST).entity("invalid JSON").build();
        }
        final String type = msg.optString(SNS_MESSAGE_TYPE, null);
        if (type == null) {
            return Response.status(Status.BAD_REQUEST).entity("no message type").build();
        }
        final String arn = rawHeaders.getFirst(HEADER_AWS_SNS_SUBSCRIPTION_ARN);

        // verify message
        if (!verifyMessage(msg, raw)) {
            return Response.status(Status.BAD_REQUEST).entity("unable to verify message").build();
        }

        LOG.debug("message: {}", msg);

        switch (type) {
        case "SubscriptionConfirmation":
            confirmSubscription(msg);
            break;
        case "Notification":
            if (verifySubscription(arn, msg)) {
                processNotification(msg);
            }
            break;
        default:
            LOG.debug("unknown message type: {}", type);
            return Response.status(Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    private boolean verifyMessage(final JSONObject msg, final String raw) {
        try {
            // make sure the message is signed by a valid domain
            final URL url = new URL(msg.getString(SNS_MESSAGE_SIGNING_URL));
            if (!validDomains.contains(url.getHost())) {
                return false;
            }

            // retrieve the PublicKey
            final InputStream in = url.openStream();
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate cert = (X509Certificate) cf.generateCertificate(in);
            final PublicKey pk = cert.getPublicKey();
            in.close();

            // verify the received message
            return snsVerify.verifyMessageSignature(raw, pk);
        } catch (final Exception e) {
            return false;
        }
    }

    private boolean verifySubscription(final String arn, final JSONObject msg) {
        final SnsSubscription subscription = this.manager.getSnsSubscription(this.getApiGroup(), arn);
        return subscription != null
                && StringUtils.equals(subscription.getTopicArn(), msg.optString(SNS_MESSAGE_TOPIC_ARN));
    }

    private boolean confirmSubscription(final JSONObject msg) {
        try {
            final String topicArn = msg.optString(SNS_MESSAGE_TOPIC_ARN);
            final String token = msg.optString(SNS_MESSAGE_TOKEN);
            final ConfirmSubscriptionResult result = this.sns.confirmSubscription(topicArn, token, "true");
            this.manager.createSnsSubscription(this.getApiGroup(), result.getSubscriptionArn(), topicArn);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    private void processNotification(final JSONObject msg) {
        LOG.debug("Notification: {}", msg);
    }
}
