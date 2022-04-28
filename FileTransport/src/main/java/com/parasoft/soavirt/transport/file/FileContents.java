package com.parasoft.soavirt.transport.file;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;

import com.parasoft.api.ICustomMessage;

public class FileContents implements ICustomMessage<String> {
    public final static String NL_FILE_PATH = "File Path"; //$NON-NLS-1$
    public final static String NL_TEXTXML = "text/xml"; //$NON-NLS-1$
    public final static String NL_UTF8 = "UTF-8"; //$NON-NLS-1$

    private byte[] content;
    private String contentMimeType;
    private String charset;
    private String filePath;

    public FileContents(byte[] bodyContent, String filePath) {
        setBodyBytes(bodyContent);
        contentMimeType = NL_TEXTXML;
        charset = NL_UTF8;
        this.filePath = filePath;
    }

    @Override
    public byte[] getBodyBytes() {
        return content;
    }

    @Override
    public String getBodyString() {
        try {
            return new String(content, charset);
        } catch (UnsupportedEncodingException e) {
            // Ignore charset
            return new String(content);
        }
    }

    @Override
    public String getContentType() {
        return contentMimeType;
    }

    @Override
    public String getHeaderField(String name) {
        return filePath;
    }

    @Override
    public Collection<String> getHeaderNames() {
        Collection<String> ret = new LinkedList<>();
        ret.add(NL_FILE_PATH);
        return ret;
    }

    @Override
    public String getHeaders() {
        return NL_FILE_PATH + ":" + filePath;
    }

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    @Override
    public void setBodyBytes(byte[] contentBytes) {
        content = contentBytes;
        if (content == null) {
            content = new byte[0];
        }
    }

    @Override
    public void setBodyString(String content) {
        try {
            this.content = content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            // Ignore charset
            this.content = content.getBytes();
        }
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public void setContentType(String mimeType) {
        contentMimeType = mimeType;
    }

    @Override
    public void setHeader(String key, String value) {
        if (NL_FILE_PATH.equals(key) && value != null) {
            filePath = value;
        }
    }
}
