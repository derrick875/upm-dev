package com.nets.nps.paynow;

import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;
import com.nets.nps.paynow.exception.ExceptionHandler;
import com.nets.nps.paynow.handlers.CreditNotificationHandler;
import com.nets.nps.paynow.handlers.OrderHandler;
import com.nets.nps.paynow.handlers.OrderQueryHandler;
import com.nets.nps.paynow.handlers.ReversalHandler;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;

@Service
public class Router {

    @Autowired
    CreditNotificationHandler creditNotificationHandler;
    
    @Autowired
    OrderHandler orderHandler;
    
    @Autowired
    OrderQueryHandler orderQueryHandler;
    
    @Autowired
    ReversalHandler reversalHandler;
    
    @Autowired
	ExceptionHandler exceptionHandler;

    public Response route(String mti, String processCode, Request request) {
        try {
            MtiRequestMapper mtiMapper = MtiRequestMapper.find(mti, processCode).get();
            notNull.test(mtiMapper).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_TYPE);

            switch (mtiMapper) {
                case CREDIT_NOTIFICATION:
                    return creditNotificationHandler.handle(mtiMapper, request);
                    
                case ORDER:
                    return orderHandler.handle(mtiMapper, request);
                    
                case ORDER_QUERY:
                	return orderQueryHandler.handle(mtiMapper, request);
               
                case ORDER_REVERSAL:	
                	return reversalHandler.handle(mtiMapper, request);
                	
                default:
                    // TODO add logic if there is no handler found
                    return null;
            }
        } catch (JsonFormatException formatEx) {
            return exceptionHandler.handleJsonFormatException(request, formatEx);
        } catch (BaseBusinessException businessEx) {
            return exceptionHandler.handleBusinessException(request, businessEx);
        } catch (Exception ex) {
            return exceptionHandler.handleGenericException(request, ex);
        }
    }

    public Response route(Request request) {
        return this.route(request.getMti(), request.getProcessCode(), request);
    }
}
