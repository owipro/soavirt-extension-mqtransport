package com.parasoft.soavirt.messages.simplemessage;

import com.parasoft.api.message.*;

import java.util.*;
import java.util.Map.Entry;
import javax.xml.parsers.*;

import org.w3c.dom.*;

public class SimpleMessageConverter implements ICustomXMLConverter {

    private final static String NL_BODY = "body";
    private final static String NL_MESSAGE = "message";

    @Override
    public INativeMessage toNative(IXMLMessage data, IConversionContext context) throws CustomConversionException {
        return new DefaultNativeMessage(createMessage(data, context).toString(), data.getCharacterEncoding(),
                data.getMessageType());
    }

    @Override
    public IXMLMessage toXML(INativeMessage data, IConversionContext context) throws CustomConversionException {
        SimpleMessage message = SimpleMessage.parse(data.getString());
        Document doc = createDocument(message, context);
        return new DefaultXMLMessage(doc, data.getCharacterEncoding(), data.getMessageType());
    }

    // create message object from XML document
    private SimpleMessage createMessage(IXMLMessage data, IConversionContext context) throws CustomConversionException {
        Document doc = data.getDOM();
        Element root = (Element) doc.getDocumentElement();
        SimpleMessage message = new SimpleMessage();
        NodeList fields = root.getElementsByTagName("body").item(0).getChildNodes();
        for (int i = 0; i < fields.getLength(); i++) {
            Node element = fields.item(i);
            if (element instanceof Element) {
                String key = element.getNodeName();
                String value = element.getTextContent();
                if (!value.isEmpty()) {
                    message.setField(key, value);
                }
            }
        }
        return message;
    }

    // convert from message to XML
    private Document createDocument(SimpleMessage message, IConversionContext context)
            throws CustomConversionException {
        Map<String, String> fields = message.getMap();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new CustomConversionException(
                    "Unable to create DocumentBuilder cause by ParserConfigurationException");
        }
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement(NL_MESSAGE);
        Element body = doc.createElement(NL_BODY);
        root.appendChild(body);
        doc.appendChild(root);
        for (Entry<String, String> entry : fields.entrySet()) {
            Element f = doc.createElement(entry.getKey());
            if (f != null) {
                f.setTextContent(entry.getValue());
                body.appendChild(f);
            } else {
                context.report("Key value is null:" + entry.getKey());
            }
        }
        return doc;

    }

}
