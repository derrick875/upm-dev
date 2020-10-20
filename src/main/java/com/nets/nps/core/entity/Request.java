package com.nets.nps.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.upos.commons.logger.Loggable;

@JsonDeserialize(using = RequestDeserializer.class)
public abstract class Request implements Loggable {
	
	@JsonProperty("mti")
    private String mti;
	
	@JsonProperty("process_code")
    private String processCode;

    public String getMti() {
        return mti;
    }

    public void setMti(String mti) {
        this.mti = mti;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }
    
    public Response createResponse(){
		return null;
	}

    @Override
    public String toString() {
        return "Request{" +
                "mti='" + mti + '\'' +
                ", processCode='" + processCode + '\'' +
                '}';
    }
}
