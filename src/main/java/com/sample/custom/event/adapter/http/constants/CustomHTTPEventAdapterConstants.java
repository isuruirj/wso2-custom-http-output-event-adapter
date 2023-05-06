package com.sample.custom.event.adapter.http.constants;

public class CustomHTTPEventAdapterConstants {

    public static final String ADAPTER_TYPE_HTTP = "customHttp";
    public static final String ADAPTER_MESSAGE_URL = "http.url";
    public static final String ADAPTER_MESSAGE_URL_HINT = "http.url.hint";
    public static final int ADAPTER_MIN_THREAD_POOL_SIZE = 8;
    public static final int ADAPTER_MAX_THREAD_POOL_SIZE = 100;
    public static final int ADAPTER_EXECUTOR_JOB_QUEUE_SIZE = 2000;
    public static final long DEFAULT_KEEP_ALIVE_TIME_IN_MILLIS = 20000;
    public static final String ADAPTER_MIN_THREAD_POOL_SIZE_NAME = "minThread";
    public static final String ADAPTER_MAX_THREAD_POOL_SIZE_NAME = "maxThread";
    public static final String ADAPTER_KEEP_ALIVE_TIME_NAME = "keepAliveTimeInMillis";
    public static final String ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME = "jobQueueSize";
    public static final String ADAPTER_PROXY_HOST = "http.proxy.host";
    public static final String ADAPTER_PROXY_HOST_HINT = "http.proxy.host.hint";
    public static final String ADAPTER_PROXY_PORT = "http.proxy.port";
    public static final String ADAPTER_PROXY_PORT_HINT = "http.proxy.port.hint";
    public static final String ADAPTER_USERNAME = "http.username";
    public static final String ADAPTER_USERNAME_HINT = "http.username.hint";
    public static final String ADAPTER_PASSWORD = "http.password";
    public static final String ADAPTER_PASSWORD_HINT = "http.password.hint";
    public static final String ADAPTER_HEADERS = "http.headers";
    public static final String ADAPTER_HEADERS_HINT = "http.headers.hint";
    public static final String HEADER_SEPARATOR = ",";
    public static final String ENTRY_SEPARATOR = ":";
    public static final String ADAPTER_HTTP_CLIENT_METHOD = "http.client.method";
    public static final String CONSTANT_HTTP_POST = "HttpPost";
    public static final String CONSTANT_HTTP_PUT = "HttpPut";

    //configurations for the httpConnectionManager
    public static final String DEFAULT_MAX_CONNECTIONS_PER_HOST = "defaultMaxConnectionsPerHost";
    public static final int DEFAULT_MAX_CONNECTIONS_PER_HOST_VALUE = 2;
    public static final String MAX_TOTAL_CONNECTIONS = "maxTotalConnections";
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    public static final String AUTHORIZATION_HEADER = "Authorization: Bearer ";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String CLIENT_ID = "clientId";
    public static final String TOKEN_URL = "tokenUrl";
}
