package com.nets.nps.core.service;

import com.nets.nps.core.entity.Request;

public interface PaymentService<P extends Request, R extends Object> {
    R process(P payment) throws Exception;
}
