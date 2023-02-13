package com.parasoft.soavirt.transport.mq;
import com.parasoft.api.ICustomMessage;
import java.util.Collection;

public class MQContents implements ICustomMessage<String> {


    private String message;

    public MQContents(String message) {
        this.message = message;
    }

	@Override
	public byte[] getBodyBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBodyString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeaderField(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBodyBytes(byte[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBodyString(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
}
