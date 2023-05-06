# wso2-custom-http-output-event-adapter

Custom HTTP Output Event Adapter to send SMS in MFA flows.

Note: This is implemented for WSO2IS-5.10.0. If you want to use it in some other WSO2 server, you can simply modify the pom.xml file’s project dependency versions matching the same version packed in the product

### Steps to deploy
- Build the component by running "mvn clean install"
- Copy following jar file which can be found in target directory into <IS_HOME>/repository/components/dropins/
    - com.sample.custom.event.adapter.http-1.0-SNAPSHOT.jar
- Configure the Output Adapter Event by adding following lines into deployment.toml file.
```
[[output_adapter.custom_output_adapter]]
type= "customHttp"
[output_adapter.custom_output_adapter.properties]
minThread= 8
maxThread= 100
keepAliveTimeInMillis= 20000
jobQueueSize= 10000
defaultMaxConnectionsPerHost= 50
maxTotalConnections = 1000
clientId = ""
clientSecret = ""
tokenUrl = ""
```
- Open the <IS-HOME>/repository/deployment/server/eventpublishers/SMSPublisher.xml file and update it as below,
```
<?xml version="1.0" encoding="UTF-8"?>
<eventPublisher name="SMSPublisher" processing="enable"
    statistics="disable" trace="disable" xmlns="http://wso2.org/carbon/eventpublisher">
    <from streamName="id_gov_sms_notify_stream" version="1.0.0"/>
    <mapping customMapping="enable" type="json">
        <inline>{"outboundSMSMessageRequest": { {{body}} }}</inline>
    </mapping>
    <to eventAdapterType="customHttp">
        <property name="http.client.method">httpPost</property>
        <property name="http.url"></property>
    </to>
</eventPublisher>
```
- Restart WSO2 IS.
