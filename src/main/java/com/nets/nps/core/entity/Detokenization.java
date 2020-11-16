package com.nets.nps.core.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nets.upos.commons.logger.Loggable;

@JsonInclude(Include.NON_NULL)
public class Detokenization implements Serializable, Loggable {

	private static final long serialVersionUID = 392083630673729172L;
	
	private DetokenizationHeader header;
	private DetokenizationBody body;
	
	public DetokenizationHeader getHeader() {
		return header;
	}
	public void setHeader(DetokenizationHeader header) {
		this.header = header;
	}
	public DetokenizationBody getBody() {
		return body;
	}
	public void setBody(DetokenizationBody body) {
		this.body = body;
	}
	@Override
	public String log() {
		String headerLog = (header !=null) ? header.log() :null;
		String bodyLog = (body !=null) ? body.log() :null;
		StringBuffer buffer = new StringBuffer();
		buffer.append("Detokenization [");
		buffer.append("header[" + headerLog + "]" );
		buffer.append("body[" + bodyLog + "]" );
		buffer.append("]");
		return buffer.toString();
	}
	@Override
	public String logKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
