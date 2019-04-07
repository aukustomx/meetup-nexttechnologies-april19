package com.aukustomx.simpleapp.common.model;

import static com.aukustomx.simpleapp.common.model.ResponseCode.SUCCESSFUL_OPERATION;

public class ResponseVO<T> {

    private final ResponseCode responseCode;
    private final T result;

    private ResponseVO(ResponseCode responseCode, T result) {
        this.responseCode = responseCode;
        this.result = result;
    }

    public static <T> ResponseVO<T> of(ResponseCode responseCode, T result) {
        return new ResponseVO<>(responseCode, result);
    }

    public static <T> ResponseVO<T> successful() {
        return new ResponseVO<>(SUCCESSFUL_OPERATION, null);
    }

    public static <T> ResponseVO<T> successful(T result) {
        return new ResponseVO<>(SUCCESSFUL_OPERATION, result);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public T getResult() {
        return result;
    }
}
