package com.nets.nps.paynow.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nets.nps.paynow.HttpController;
import com.nets.nps.paynow.exception.ExceptionHandler;
import com.nets.nps.paynow.exception.InvalidRequestError;
import com.nets.nps.paynow.exception.RestResponseEntityExceptionHandler;
import com.nets.nps.paynow.security.RequestWithMACWrapper;
import com.nets.nps.paynow.security.service.HSMService;
import com.nets.upos.commons.logger.ApsLogger;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class HSMFilter implements Filter {

    private static final ApsLogger logger = new ApsLogger(HSMFilter.class);

    private static final String HEADER_NETS_KSN = "NETS-KSN";
    private static final String DUKPT_ERROR = "DUKPT Error.";
    private static final String MAC_ERROR = "Invalid MAC.";

    @Autowired
    ObjectMapper jacksonObjectMapper;

    @Autowired
    HSMService strongKeyService;

    @Autowired
    ExceptionHandler exceptionHandler;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            String mti = req.getHeader(HttpController.HEADER_NETS_MTI);
            String processCode = req.getHeader(HttpController.HEADER_NETS_PROCESSCODE);
            String ksn = req.getHeader(HEADER_NETS_KSN);
            logger.debug("HTTP Method: " + req.getMethod() +  ", ContentType: " + req.getContentType() + ", ksn: " + ksn);

            if (HttpMethod.POST.matches(req.getMethod()) &&
                    req.getContentType().contains(MediaType.TEXT_PLAIN_VALUE)) {
                logger.info("Decrypt request.");
                long t = System.currentTimeMillis();

                String body;
                try {
                    body = strongKeyService.decrypt(ksn, req.getInputStream());
                } catch (Exception e) {
                    throw new Exception(DUKPT_ERROR, e);
                }

                RequestWithMACWrapper wrapper = new RequestWithMACWrapper(body);
                String payload = wrapper.getPayload();
                String mac = wrapper.getTxnMac();
                // Assume payload is utf-8 encoded
                String sha256 = DigestUtils.sha256Hex(payload);
                logger.info("payload: " + payload);
                logger.info("txnMac: " + mac + ", SHA256: " + sha256);
                if (strongKeyService.verify(ksn, sha256, mac)) {
                    throw new Exception(MAC_ERROR);
                }

                DecryptedHttpRequestWrapper requestWrapper =
                        new DecryptedHttpRequestWrapper(req, payload);
                chain.doFilter(requestWrapper, res);

                logger.info(String.format("[Mti %s Process Code %s] - Execution Time : %d ms.",
                        mti, processCode, (System.currentTimeMillis() - t)));
            } else {
                // TODO: remove this condition after SIT
                chain.doFilter(request, response);
            }
        } catch (Exception ex) {
            HttpServletResponse res = (HttpServletResponse) response;

            if(ex.getMessage() != null
                    && (ex.getMessage().equals(DUKPT_ERROR) || ex.getMessage().equals(MAC_ERROR))) {
                logger.error("HTTP 406 Not Acceptable. Not readable http message.", ex);
//                exceptionHandler.createErrorEvent("HSMFilter Exception", null,
//                        "HTTP 406 Not Acceptable. Not readable http message. " + ex);
                res.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                logger.error("HTTP 400 Bad Request. Not readable http message.", ex);
//                exceptionHandler.createErrorEvent("HSMFilter Exception", null,
//                        "HTTP 400 Bad Request. Not readable http message. " + ex);
                res.setStatus(HttpStatus.BAD_REQUEST.value());
            }

            res.getWriter().write(
                    jacksonObjectMapper.writeValueAsString(
                            new InvalidRequestError(RestResponseEntityExceptionHandler.BAD_REQUEST_MESSAGE,
                                    ex.getMessage())));
        }
    }
}

