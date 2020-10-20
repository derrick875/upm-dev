package com.nets.nps.paynow.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class DecryptedHttpRequestWrapper extends HttpServletRequestWrapper {

    private final ByteArrayInputStream decryptedDataStream;

    public DecryptedHttpRequestWrapper(HttpServletRequest request, String decryptedData) {
        super(request);
        // TODO get charset from constant or system property
        this.decryptedDataStream = new ByteArrayInputStream(decryptedData.getBytes(Charset.forName("UTF-8")));
    }

    @Override
    public BufferedReader getReader() throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(decryptedDataStream));
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }

    @Override
    public String getHeader(String name) {
        if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(name)) {
            return MediaType.APPLICATION_JSON_VALUE;
        } else {
            return super.getHeader(name);
        }
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(name)) {
            Vector<String> v = new Vector<>();
            for (Enumeration<String> headers = super.getHeaders(name);
                 headers.hasMoreElements();) {
                String header = headers.nextElement();
                if (Objects.equals(MediaType.TEXT_PLAIN_VALUE, header)) {
                    v.add(MediaType.APPLICATION_JSON_VALUE);
                } else {
                    v.add(headers.nextElement());
                }
            }
            return v.elements();
        } else {
            return super.getHeaders(name);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            @Override
            public int read() {
                return decryptedDataStream.read();
            }

            @Override
            public boolean isFinished() {
                return decryptedDataStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException("Not implemented");
            }
        };
    }

}
