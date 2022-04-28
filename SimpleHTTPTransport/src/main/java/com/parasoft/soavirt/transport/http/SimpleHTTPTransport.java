package com.parasoft.soavirt.transport.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.parasoft.api.ICustomMessage;
import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;
import com.parasoft.api.transport.ICustomConnection;
import com.parasoft.api.transport.ICustomTransport;

public class SimpleHTTPTransport implements ICustomTransport {
    private final static String NL_CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$
    private final static String NL_INPUTSTREAM_ERR = "Failed to get an input stream, error stream output shall be used: "; //$NON-NLS-1$

    public SimpleHTTPTransport() {
    }

    @Override
    public ICustomConnection createNewConnection(CustomTransportConfiguration transportConfig) throws Throwable {
        return new SimpleHTTPConnection();
    }

    @Override
    public ICustomMessage invoke(ICustomConnection transportConnection, CustomTransportConfiguration transportConfig, ICustomMessage request, ScriptingContext context) throws Throwable {
        SimpleHTTPConnection con = (SimpleHTTPConnection)transportConnection;
        SimpleHTTPConfiguration config = new SimpleHTTPConfiguration(transportConfig);
        HttpURLConnection httpUrlconnection;
        if (con == null) {
            con = (SimpleHTTPConnection)createNewConnection(transportConfig);
            con.connect(transportConfig, context);
            httpUrlconnection = con.getConnection();
            httpUrlconnection.setRequestProperty(NL_CONTENT_TYPE, request.getContentType());
        } else {
            httpUrlconnection = con.getConnection();
        }
        if (config.getHeaderName(context) != null) {
            httpUrlconnection.setRequestProperty(config.getHeaderName(context), config.getHeaderValue(context));
        }
        for (Map.Entry<String, List<String>> entry : httpUrlconnection.getRequestProperties().entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
        if (request.getHeaderField(SimpleHTTPConnection.NL_CONNECTION) == null) {
            // it seem like in the past httpUrlconnection.getRequestProperties() used to return the "Connection"
            // property/header value but now it does not. This might be related to
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6973030
            // so we are setting this here manually so the header value gets exposure through the request object
            if (transportConfig.isConnectionKeepAlive()) {
                request.setHeader(SimpleHTTPConnection.NL_CONNECTION, SimpleHTTPConnection.NL_KEEP_ALIVE);
            } else {
                request.setHeader(SimpleHTTPConnection.NL_CONNECTION, SimpleHTTPConnection.NL_CLOSE);
            }
        }
        try (DataOutputStream os = new DataOutputStream(httpUrlconnection.getOutputStream())) {
            os.write(request.getBodyBytes());
        }
        InputStream is;
        try {
            is = httpUrlconnection.getInputStream();
        } catch (IOException e) {
            context.report(NL_INPUTSTREAM_ERR + e.getMessage());
            is = httpUrlconnection.getErrorStream();
        }
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader responseReader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBody.append(line);
                responseBody.append('\n');
            }
        }
        // get response headers in order, unlike java.net.URLConnection.getHeaderFields()
        Map<String, List<String>> responseHeaderFields = new LinkedHashMap<>();
        String headerName = null;
        for (int i = 0; (headerName = httpUrlconnection.getHeaderFieldKey(i)) != null || i == 0; i++) {
            List<String> values = responseHeaderFields.get(headerName);
            if (values == null) {
                values = new ArrayList<>();
                responseHeaderFields.put(headerName, values);
            }
            values.add(httpUrlconnection.getHeaderField(i));
        }
        SimpleMessage response = new SimpleMessage(responseBody.toString(), responseHeaderFields);
        return response;
    }
}
