package com.aukustomx.simpleapp.infra.exception;

import com.aukustomx.simpleapp.common.model.ResponseCode;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserException extends RuntimeException {

    private ResponseCode responseCode;

    public UserException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public UserException(String message, ResponseCode responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    public UserException(String message, Throwable cause, ResponseCode responseCode) {
        super(message, cause);
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}
