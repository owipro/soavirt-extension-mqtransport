package com.parasoft.soavirt.messages.simplemessage;

import java.util.*;

import com.parasoft.api.message.CustomConversionException;

public class SimpleMessage {

    private Map<String, String> fields = new LinkedHashMap<String, String>();

    public static SimpleMessage parse(String message) throws CustomConversionException {
        SimpleMessage sm = new SimpleMessage();
        Scanner scanner = new Scanner(message);
        scanner.useDelimiter(" ");
        while (scanner.hasNext()) {
            String section = scanner.next();
            String[] pair = section.split("=");
            String key = pair[0];
            String value = pair[1];
            sm.setField(key, value);
        }
        return sm;
    }

    public void setField(String key, String value) throws CustomConversionException {
        if (key.equals("")) {
            throw new CustomConversionException("Key entered is empty");
        }
        if (value.equals("")) {
            throw new CustomConversionException("Value entered is empty");
        }
        fields.put(key, value);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : fields.keySet()) {
            builder.append(key).append('=').append(fields.get(key)).append(' ');
        }
        String message = builder.toString();
        return message.trim();
    }

    public Map<String, String> getMap() {
        return fields;
    }

}
