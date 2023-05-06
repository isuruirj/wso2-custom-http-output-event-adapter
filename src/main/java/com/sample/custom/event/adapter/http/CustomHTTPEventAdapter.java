package com.sample.custom.event.adapter.http;

import com.sample.custom.event.adapter.http.constants.CustomHTTPEventAdapterConstants;
import org.apache.axiom.om.util.Base64;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.event.output.adapter.core.EventAdapterUtil;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapter;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapterConfiguration;
import org.wso2.carbon.event.output.adapter.core.exception.ConnectionUnavailableException;
import org.wso2.carbon.event.output.adapter.core.exception.OutputEventAdapterException;
import org.wso2.carbon.event.output.adapter.core.exception.TestConnectionNotSupportedException;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * This class implements the OutputEvent Adapter Interface as a custom HTTP Event Adapter
 */
public class CustomHTTPEventAdapter implements OutputEventAdapter {

    private static final Log log = LogFactory.getLog(CustomHTTPEventAdapter.class);
    private OutputEventAdapterConfiguration eventAdapterConfiguration;
    private Map<String, String> globalProperties;
    private static ExecutorService executorService;
    private String clientMethod;
    private String proxyHost = null;
    private String proxyPort = null;
    private int tenantId;
    private String contentType;
    private static HttpConnectionManager connectionManager;
    private HttpClient httpClient = null;
    private HostConfiguration hostConfiguration = null;

    public CustomHTTPEventAdapter(OutputEventAdapterConfiguration eventAdapterConfiguration,
                                  Map<String, String> globalProperties) {

        this.eventAdapterConfiguration = eventAdapterConfiguration;
        this.globalProperties = globalProperties;
        this.clientMethod = eventAdapterConfiguration.getStaticProperties()
                .get(CustomHTTPEventAdapterConstants.ADAPTER_HTTP_CLIENT_METHOD);
        // Setting the static proxy configurations for the HTTP adapter.
        if (eventAdapterConfiguration.getStaticProperties().get(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_HOST) != null &&
                eventAdapterConfiguration.getStaticProperties().get(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_PORT) != null) {
            this.proxyPort =
                    eventAdapterConfiguration.getStaticProperties().get(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_PORT);
            this.proxyHost =
                    eventAdapterConfiguration.getStaticProperties().get(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_HOST);
        }
    }

    @Override
    public void init() throws OutputEventAdapterException {

        tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();

        //ExecutorService will be assigned  if it is null
        if (executorService == null) {
            int minThread;
            int maxThread;
            long defaultKeepAliveTime;
            int jobQueSize;

            //If global properties are available those will be assigned else constant values will be assigned
            if (globalProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_MIN_THREAD_POOL_SIZE_NAME) != null) {
                minThread = Integer
                        .parseInt(globalProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_MIN_THREAD_POOL_SIZE_NAME));
            } else {
                minThread = CustomHTTPEventAdapterConstants.ADAPTER_MIN_THREAD_POOL_SIZE;
            }

            if (globalProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_MAX_THREAD_POOL_SIZE_NAME) != null) {
                maxThread = Integer
                        .parseInt(globalProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_MAX_THREAD_POOL_SIZE_NAME));
            } else {
                maxThread = CustomHTTPEventAdapterConstants.ADAPTER_MAX_THREAD_POOL_SIZE;
            }

            if (globalProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_KEEP_ALIVE_TIME_NAME) != null) {
                defaultKeepAliveTime = Integer
                        .parseInt(globalProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_KEEP_ALIVE_TIME_NAME));
            } else {
                defaultKeepAliveTime = CustomHTTPEventAdapterConstants.DEFAULT_KEEP_ALIVE_TIME_IN_MILLIS;
            }

            if (globalProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME) != null) {
                jobQueSize = Integer
                        .parseInt(globalProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME));
            } else {
                jobQueSize = CustomHTTPEventAdapterConstants.ADAPTER_EXECUTOR_JOB_QUEUE_SIZE;
            }
            executorService = new ThreadPoolExecutor(minThread, maxThread, defaultKeepAliveTime, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(jobQueSize));

            //configurations for the httpConnectionManager which will be shared by every http adapter
            int defaultMaxConnectionsPerHost;
            int maxTotalConnections;

            if (globalProperties.get(CustomHTTPEventAdapterConstants.DEFAULT_MAX_CONNECTIONS_PER_HOST) != null) {
                defaultMaxConnectionsPerHost = Integer
                        .parseInt(globalProperties.get(CustomHTTPEventAdapterConstants.DEFAULT_MAX_CONNECTIONS_PER_HOST));
            } else {
                defaultMaxConnectionsPerHost = CustomHTTPEventAdapterConstants.DEFAULT_MAX_CONNECTIONS_PER_HOST_VALUE;
            }

            if (globalProperties.get(CustomHTTPEventAdapterConstants.MAX_TOTAL_CONNECTIONS) != null) {
                maxTotalConnections = Integer
                        .parseInt(globalProperties.get(CustomHTTPEventAdapterConstants.MAX_TOTAL_CONNECTIONS));
            } else {
                maxTotalConnections = CustomHTTPEventAdapterConstants.DEFAULT_MAX_TOTAL_CONNECTIONS;
            }

            connectionManager = new MultiThreadedHttpConnectionManager();
            connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnectionsPerHost);
            connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);

        }
    }

    @Override
    public void testConnect() throws TestConnectionNotSupportedException, ConnectionUnavailableException {
        throw new TestConnectionNotSupportedException("Test connection is not available");
    }

    @Override
    public void connect() throws ConnectionUnavailableException {

        this.checkHTTPClientInit(eventAdapterConfiguration.getStaticProperties());
    }

    @Override
    public void publish(Object message, Map<String, String> dynamicProperties) throws ConnectionUnavailableException {

        //Load dynamic properties
        String url = dynamicProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_MESSAGE_URL);
        String username = dynamicProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_USERNAME);
        String password = dynamicProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_PASSWORD);
        Map<String, String> headers = this
                .extractHeaders(dynamicProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_HEADERS));
        if (MapUtils.isEmpty(headers)) {
            headers = new HashMap<>();
        }
        headers.put(CustomHTTPEventAdapterConstants.AUTHORIZATION_HEADER, getOauth2Token(
                globalProperties.get(CustomHTTPEventAdapterConstants.CLIENT_ID), globalProperties
                        .get(CustomHTTPEventAdapterConstants.CLIENT_SECRET), globalProperties
                        .get(CustomHTTPEventAdapterConstants.TOKEN_URL)));
        String payload = message.toString();

        try {
            executorService.submit(new HTTPSender(url, payload, username, password, headers, httpClient));
        } catch (RejectedExecutionException e) {
            EventAdapterUtil
                    .logAndDrop(eventAdapterConfiguration.getName(), message, "Job queue is full", e, log, tenantId);
        }
    }

    @Override
    public void disconnect() {
        //not required
    }

    @Override
    public void destroy() {
        //not required
    }

    @Override
    public boolean isPolled() {

        return false;
    }

    private void checkHTTPClientInit(Map<String, String> staticProperties) {

        if (this.httpClient != null) {
            return;
        }

        synchronized (CustomHTTPEventAdapter.class) {
            if (this.httpClient != null) {
                return;
            }

            httpClient = new HttpClient(connectionManager);
            String proxyHost = staticProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_HOST);
            String proxyPort = staticProperties.get(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_PORT);
            if (proxyHost != null && proxyHost.trim().length() > 0) {
                try {
                    HttpHost host = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
                    this.httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
                } catch (NumberFormatException e) {
                    log.error("Invalid proxy port: " + proxyPort + ", "
                            + "ignoring proxy settings for HTTP output event adaptor...");
                }
            }

            String messageFormat = eventAdapterConfiguration.getMessageFormat();
            if (messageFormat.equalsIgnoreCase("json")) {
                contentType = "application/json";
            } else if (messageFormat.equalsIgnoreCase("text")) {
                contentType = "text/plain";
            } else {
                contentType = "text/xml";
            }

        }

    }

    private Map<String, String> extractHeaders(String headers) {
        if (headers == null || headers.trim().length() == 0) {
            return null;
        }

        String[] entries = headers.split(CustomHTTPEventAdapterConstants.HEADER_SEPARATOR);
        String[] keyValue;
        Map<String, String> result = new HashMap<String, String>();
        for (String header : entries) {
            try {
                keyValue = header.split(CustomHTTPEventAdapterConstants.ENTRY_SEPARATOR, 2);
                result.put(keyValue[0].trim(), keyValue[1].trim());
            } catch (Exception e) {
                log.warn("Header property '" + header + "' is not defined in the correct format.", e);
            }
        }
        return result;
    }

    /**
     * This class represents a job to send an HTTP request to a target URL.
     */
    class HTTPSender implements Runnable {

        private String url;

        private String payload;

        private String username;

        private String password;

        private Map<String, String> headers;

        private HttpClient httpClient;

        public HTTPSender(String url, String payload, String username, String password, Map<String, String> headers,
                          HttpClient httpClient) {
            this.url = url;
            this.payload = payload;
            this.username = username;
            this.password = password;
            this.headers = headers;
            this.httpClient = httpClient;
        }

        public String getUrl() {
            return url;
        }

        public String getPayload() {
            return payload;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public HttpClient getHttpClient() {
            return httpClient;
        }

        public void run() {

            EntityEnclosingMethod method = null;

            try {

                if (clientMethod.equalsIgnoreCase(CustomHTTPEventAdapterConstants.CONSTANT_HTTP_PUT)) {
                    method = new PutMethod(this.getUrl());
                } else {
                    method = new PostMethod(this.getUrl());
                }

                if (hostConfiguration == null) {
                    URL hostUrl = new URL(this.getUrl());
                    hostConfiguration = new HostConfiguration();
                    hostConfiguration.setHost(hostUrl.getHost(), hostUrl.getPort(), hostUrl.getProtocol());
                    if (StringUtils.isNotBlank(proxyHost) && StringUtils.isNotBlank(proxyPort)) {
                        hostConfiguration.setProxy(proxyHost, Integer.parseInt(proxyPort));
                    }
                }

                method.setRequestEntity(new StringRequestEntity(this.getPayload(), contentType, "UTF-8"));

                if (this.getUsername() != null && this.getUsername().trim().length() > 0) {
                    method.setRequestHeader("Authorization", "Basic " + Base64
                            .encode((this.getUsername() + CustomHTTPEventAdapterConstants.ENTRY_SEPARATOR + this
                                    .getPassword()).getBytes()));
                }

                if (this.getHeaders() != null) {
                    for (Map.Entry<String, String> header : this.getHeaders().entrySet()) {
                        method.setRequestHeader(header.getKey(), header.getValue());
                    }
                }

                this.getHttpClient().executeMethod(hostConfiguration, method);

            } catch (UnknownHostException e) {
                EventAdapterUtil.logAndDrop(eventAdapterConfiguration.getName(), this.getPayload(),
                        "Cannot connect to " + this.getUrl(), e, log, tenantId);
            } catch (Throwable e) {
                EventAdapterUtil
                        .logAndDrop(eventAdapterConfiguration.getName(), this.getPayload(), null, e, log, tenantId);
            } finally {
                if (method != null) {
                    method.releaseConnection();
                }
            }
        }
    }

    private String getOauth2Token(String clientId, String clientSecret, String tokenUrl) {

        //TODO implement this method to get the oauth2 token
        return "XXXXX";
    }
}
