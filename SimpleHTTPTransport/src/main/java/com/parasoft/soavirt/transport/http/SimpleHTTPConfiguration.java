package com.parasoft.soavirt.transport.http;

import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;

/**
 * Decorator/wrapper class for getting the values specific to SimpleHTTPTransport more easily
 * out of CustomTransportConfiguration
 */
public class SimpleHTTPConfiguration {
    // version 3
    public final static String NL_URL = "URL"; //$NON-NLS-1$
    public final static String NL_HEADER_NAME = "HeaderName"; //$NON-NLS-1$
    public final static String NL_HEADER_VALUE = "HeaderValue"; //$NON-NLS-1$

    private final CustomTransportConfiguration config;

    public SimpleHTTPConfiguration(CustomTransportConfiguration config) {
        this.config = config;
    }

    public String getURL(ScriptingContext context) {
        return config.get(NL_URL, context);
    }

    public String getHeaderName(ScriptingContext context) {
        return config.get(NL_HEADER_NAME, context);
    }

    public String getHeaderValue(ScriptingContext context) {
        return config.get(NL_HEADER_VALUE, context);
    }
}
