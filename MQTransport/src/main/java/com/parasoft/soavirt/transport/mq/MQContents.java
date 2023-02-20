package com.parasoft.soavirt.transport.mq;
import java.util.List;
import java.util.Map;

import com.parasoft.api.DefaultCustomMessage;

public class MQContents extends DefaultCustomMessage<List<String>> {

    public MQContents(String bodyContent, Map<String, List<String>> mqHeaders) {
        super(bodyContent, mqHeaders);
    }

    @Override
    public String getHeaders() {
        if (getHeadersMap() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : getHeadersMap().entrySet()) {
            if (sb.length() > 0) {
                sb.append("\r\n");
            }
            if (entry.getKey() != null) {

                sb.append(entry.getKey());
                sb.append(": ");
            }
            List<String> values = entry.getValue();
            boolean first = true;
            for (String value : values) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(value);
            }
        }
        return sb.toString();
    }
}