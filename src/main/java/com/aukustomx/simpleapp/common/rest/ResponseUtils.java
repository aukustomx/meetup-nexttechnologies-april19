package com.aukustomx.simpleapp.common.rest;

import com.aukustomx.simpleapp.common.model.ResponseVO;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ResponseUtils {

    public static Response responseOf(ResponseVO responseVO) {
        return Response
                .status(responseVO.getResponseCode().getStatus())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(responseVO.getResult())
                .build();
    }
}
