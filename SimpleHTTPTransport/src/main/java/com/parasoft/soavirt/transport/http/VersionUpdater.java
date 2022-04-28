package com.parasoft.soavirt.transport.http;

import com.parasoft.api.ICustomConfiguration;
import com.parasoft.api.IVersionUpdater;

public class VersionUpdater implements IVersionUpdater {
    // version 1
    public final static String NL_HEADER_V1 = "Header"; //$NON-NLS-1$
    // version 2
    public final static String NL_HEADER_NAME_V2 = "Header Name"; //$NON-NLS-1$
    public final static String NL_HEADER_VALUE_V2 = "Header Value"; //$NON-NLS-1$

    @Override
    public void updateVersion(int savedVersion, ICustomConfiguration config) {
        String value;
        switch (savedVersion) {
        default:
        case 1:
            value = config.getString(NL_HEADER_V1);
            if (value != null && value.contains("=")) {
                String[] parts = value.split("=");
                config.setString(SimpleHTTPConfiguration.NL_HEADER_NAME, parts[0]);
                config.setString(SimpleHTTPConfiguration.NL_HEADER_VALUE, parts[1]);
            }
        case 2:
            value = config.getString(NL_HEADER_NAME_V2);
            if (value != null) {
                config.setString(SimpleHTTPConfiguration.NL_HEADER_NAME, value);
            }
            value = config.getString(NL_HEADER_VALUE_V2);
            if (value != null) {
                config.setString(SimpleHTTPConfiguration.NL_HEADER_VALUE, value);
            }
        }
    }
}
