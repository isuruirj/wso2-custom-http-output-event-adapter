package com.sample.custom.event.adapter.http.internal;

import com.sample.custom.event.adapter.http.CustomHTTPEventAdapterFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapterFactory;

/**
 * This class implements the service component for custom http event adapter
 */
@Component(
        name = "com.sample.custom.event.adapter.http",
        immediate = true)
public class CustomHTTPEventAdapterServiceDS {

    private static final Log log = LogFactory.getLog(CustomHTTPEventAdapterServiceDS.class);

    @Activate
    protected void activate(ComponentContext context) {

        try {
            CustomHTTPEventAdapterFactory customHTTPEventAdapterFactory = new CustomHTTPEventAdapterFactory();
            context.getBundleContext().registerService(OutputEventAdapterFactory.class.getName(),
                    customHTTPEventAdapterFactory, null);
            if (log.isDebugEnabled()) {
                log.debug("Successfully deployed the custom output HTTP event adaptor service");
            }
        } catch (Throwable e) {
            log.error("Can not create the custom output HTTP event adaptor service: " + e.getMessage(), e);
        }
    }
}
