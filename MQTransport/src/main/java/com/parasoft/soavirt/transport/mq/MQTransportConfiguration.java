package com.parasoft.soavirt.transport.mq;

import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;

/**
 * Decorator/wrapper class for getting the values specific to MQTransport more easily
 * out of CustomTransportConfiguration
 */
public class MQTransportConfiguration {
    private final static String NL_QUEUE_QM = "qM";
    private final static String NL_QUEUE_PUT = "putQueue";
    private final static String NL_QUEUE_GET = "getQueue"; 

    private final CustomTransportConfiguration config;

    public MQTransportConfiguration(CustomTransportConfiguration config) {
        this.config = config;
    }

    public String getQueueManager(ScriptingContext context) {
        return config.get(NL_QUEUE_QM, context);
    }

    public String getPutQueue(ScriptingContext context) {
        return config.get(NL_QUEUE_PUT, context);
    }

    public String getGetQueue(ScriptingContext context) {
        return config.get(NL_QUEUE_GET, context);
    }
}
