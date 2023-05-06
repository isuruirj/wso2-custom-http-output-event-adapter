package com.sample.custom.event.adapter.http;

import com.sample.custom.event.adapter.http.constants.CustomHTTPEventAdapterConstants;
import org.wso2.carbon.event.output.adapter.core.MessageType;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapter;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapterFactory;
import org.wso2.carbon.event.output.adapter.core.Property;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapterConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Map;

/**
 * The http event adapter factory class to create a http output adapter
 */
public class CustomHTTPEventAdapterFactory extends OutputEventAdapterFactory {

    private ResourceBundle resourceBundle = ResourceBundle.getBundle(
            "org.wso2.carbon.event.output.adapter.http.i18n.Resources", Locale.getDefault());

    @Override
    public String getType() {

        return CustomHTTPEventAdapterConstants.ADAPTER_TYPE_HTTP;
    }

    @Override
    public List<String> getSupportedMessageFormats() {

        List<String> supportedMessageFormats = new ArrayList<String>();
        supportedMessageFormats.add(MessageType.TEXT);
        supportedMessageFormats.add(MessageType.XML);
        supportedMessageFormats.add(MessageType.JSON);

        return supportedMessageFormats;
    }

    @Override
    public List<Property> getStaticPropertyList() {

        List<Property> staticPropertyList = new ArrayList<Property>();

        Property proxyHostProp = new Property(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_HOST);
        proxyHostProp.setDisplayName(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_HOST));
        proxyHostProp.setHint(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_HOST_HINT));
        proxyHostProp.setRequired(false);

        Property proxyPortProp = new Property(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_PORT);
        proxyPortProp.setDisplayName(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_PORT));
        proxyPortProp.setHint(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_PROXY_PORT_HINT));
        proxyPortProp.setRequired(false);

        Property clientMethod = new Property(CustomHTTPEventAdapterConstants.ADAPTER_HTTP_CLIENT_METHOD);
        clientMethod.setDisplayName(
                resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_HTTP_CLIENT_METHOD));
        clientMethod.setRequired(true);
        clientMethod.setOptions(new String[]{CustomHTTPEventAdapterConstants.CONSTANT_HTTP_POST,
                CustomHTTPEventAdapterConstants.CONSTANT_HTTP_PUT});
        clientMethod.setDefaultValue(CustomHTTPEventAdapterConstants.CONSTANT_HTTP_POST);

        staticPropertyList.add(proxyHostProp);
        staticPropertyList.add(proxyPortProp);
        staticPropertyList.add(clientMethod);

        return staticPropertyList;
    }

    @Override
    public List<Property> getDynamicPropertyList() {

        List<Property> dynamicPropertyList = new ArrayList<Property>();

        Property urlProp = new Property(CustomHTTPEventAdapterConstants.ADAPTER_MESSAGE_URL);
        urlProp.setDisplayName(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_MESSAGE_URL));
        urlProp.setHint(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_MESSAGE_URL_HINT));
        urlProp.setRequired(true);

        Property usernameProp = new Property(CustomHTTPEventAdapterConstants.ADAPTER_USERNAME);
        usernameProp.setDisplayName(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_USERNAME));
        usernameProp.setHint(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_USERNAME_HINT));
        usernameProp.setRequired(false);

        Property passwordProp = new Property(CustomHTTPEventAdapterConstants.ADAPTER_PASSWORD);
        passwordProp.setDisplayName(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_PASSWORD));
        passwordProp.setHint(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_PASSWORD_HINT));
        passwordProp.setRequired(false);
        passwordProp.setSecured(true);
        passwordProp.setEncrypted(true);

        Property headersProp = new Property(CustomHTTPEventAdapterConstants.ADAPTER_HEADERS);
        headersProp.setDisplayName(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_HEADERS));
        headersProp.setHint(resourceBundle.getString(CustomHTTPEventAdapterConstants.ADAPTER_HEADERS_HINT));
        headersProp.setRequired(false);

        dynamicPropertyList.add(urlProp);
        dynamicPropertyList.add(usernameProp);
        dynamicPropertyList.add(passwordProp);
        dynamicPropertyList.add(headersProp);

        return dynamicPropertyList;
    }

    @Override
    public String getUsageTips() {
        return null;
    }

    @Override
    public OutputEventAdapter createEventAdapter(OutputEventAdapterConfiguration outputEventAdapterConfiguration,
                                                 Map<String, String> globalProperties) {

        return new CustomHTTPEventAdapter(outputEventAdapterConfiguration, globalProperties);
    }
}
