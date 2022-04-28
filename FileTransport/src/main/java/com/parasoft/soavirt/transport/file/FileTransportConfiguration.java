package com.parasoft.soavirt.transport.file;

import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;

/**
 * Decorator/wrapper class for getting the values specific to FileTransport more easily
 * out of CustomTransportConfiguration
 */
public class FileTransportConfiguration {
    private final static String NL_INPUT_FILE = "Input File"; //$NON-NLS-1$
    private final static String NL_OUTPUT_FILE = "Output File"; //$NON-NLS-1$
    private final static String NL_WAIT_TIME = "Wait Time"; //$NON-NLS-1$

    private final CustomTransportConfiguration config;

    public FileTransportConfiguration(CustomTransportConfiguration config) {
        this.config = config;
    }

    public String getInputFilePath(ScriptingContext context) {
        return config.get(NL_INPUT_FILE, context);
    }

    public String getOutputFilePath(ScriptingContext context) {
        return config.get(NL_OUTPUT_FILE, context);
    }

    public String getWaitTime(ScriptingContext context) {
        return config.get(NL_WAIT_TIME, context);
    }
}
