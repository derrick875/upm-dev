package com.nets.nps.paynow;

import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;

public interface RequestHandler {
    Response handle(MtiRequestMapper mtiMapper, Request request)
        throws Exception;
}
