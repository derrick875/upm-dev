package com.nets.nps.core.service;

import com.nets.nps.core.entity.Query;

public interface EnquiryService<Q extends Query, R> {
    R answer(Q query) throws Exception;
}
