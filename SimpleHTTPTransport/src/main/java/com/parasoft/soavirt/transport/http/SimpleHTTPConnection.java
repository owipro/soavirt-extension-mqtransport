package com.parasoft.soavirt.transport.http;

import java.net.HttpURLConnection;
import java.net.URL;

import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;
import com.parasoft.api.transport.ICustomConnection;

public class SimpleHTTPConnection implements ICustomConnection {
    private static final String NL_POST = "POST"; //$NON-NLS-1$
    public static final String NL_CONNECTION = "Connection"; //$NON-NLS-1$
    public static final String NL_CLOSE = "Close"; //$NON-NLS-1$
    public static final String NL_KEEP_ALIVE = "Keep-Alive"; //$NON-NLS-1$
    private HttpURLConnection connection; // parasoft-suppress OWASP2017.A3.USC "must suppport SSL or non-SSL"

    public SimpleHTTPConnection() {
    }

    @Override
    public void connect(CustomTransportConfiguration transportConfig, ScriptingContext context) throws Throwable {
        SimpleHTTPConfiguration config = new SimpleHTTPConfiguration(transportConfig);
        String urlString = config.getURL(context);
        URL url = new URL(urlString);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod(NL_POST);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setAllowUserInteraction(false);
        if (transportConfig.isConnectionKeepAlive()) {
            connection.setRequestProperty(NL_CONNECTION, NL_KEEP_ALIVE);
        } else {
            connection.setRequestProperty(NL_CONNECTION, NL_CLOSE);
        }
    }

    @Override
    public void close(ScriptingContext context) throws Throwable {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public HttpURLConnection getConnection() {
        return connection;
    }
}
