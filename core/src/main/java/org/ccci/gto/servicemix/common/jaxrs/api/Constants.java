package org.ccci.gto.servicemix.common.jaxrs.api;

public final class Constants {
    public static final String PATH_API_KEY = "{apiKey:[0-9a-zA-Z_\\-]+}";
    public static final String PATH_OPTIONAL_API_KEY = "{apiKey:(?:[0-9a-zA-Z_\\-]+/)?}";
    public static final String PATH_SESSION = "{sessionId:[0-9a-f]+}";
    public static final String PATH_OPTIONAL_SESSION = "{sessionId:(?:[0-9a-f]+/)?}";

    // path params
    public static final String PARAM_API_KEY = "apiKey";
    public static final String PARAM_SESSION = "sessionId";
    public static final String PARAM_TICKET = "ticket";
    public static final String PARAM_GUEST = "guest";

    // query params
    public static final String PARAM_ACCESS_TOKEN = "access_token";
}
