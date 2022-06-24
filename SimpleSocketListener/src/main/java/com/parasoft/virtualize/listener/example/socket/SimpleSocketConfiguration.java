package com.parasoft.virtualize.listener.example.socket;

import com.parasoft.api.Application;
import com.parasoft.api.responder.ICustomMessageListenerConfiguration;

public class SimpleSocketConfiguration {
    public final static String NL_PORT = "Port";
    public final static String NL_EOF = "EOF Token";

    private final ICustomMessageListenerConfiguration config;

    public SimpleSocketConfiguration(ICustomMessageListenerConfiguration config) {
        this.config = config;
    }

    public int getPort() {
        int port = -1;
        String portStr = config.getValue(NL_PORT);
        try {
            if (portStr != null && portStr.length() > 0) {
                port = Integer.parseInt(portStr);
            }
        } catch (NumberFormatException e) {
            Application.showMessage("Invalid socket port provided: " + portStr);
        }
        return port;
    }

    public String getEOFToken() {
        return config.getValue(NL_EOF);
    }
}