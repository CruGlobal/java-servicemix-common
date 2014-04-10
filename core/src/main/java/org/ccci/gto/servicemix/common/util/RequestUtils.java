package org.ccci.gto.servicemix.common.util;

import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.jaxrs.ext.MessageContext;

public class RequestUtils {
    public static boolean defaultAccept(final MessageContext cxt, HttpHeaders headers) {
        if (headers == null && cxt != null) {
            headers = cxt.getHttpHeaders();
        }

        if (headers != null) {
            // check to see if the user didn't provide an Accept header
            return headers.getRequestHeader(HttpHeaders.ACCEPT).isEmpty();
        } else {
            return false;
        }
    }
}
