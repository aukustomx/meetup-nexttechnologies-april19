package com.aukustomx.simpleapp.common.model;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public enum ResponseCode {
    SUCCESSFUL_OPERATION(Response.Status.OK, "1", "Operación exitosa"),
    FAILED_OPERATION(Response.Status.INTERNAL_SERVER_ERROR, "2", "Operación fallida"),
    USER_DOES_NOT_EXISTS(Response.Status.BAD_REQUEST, "3", "El usuario no existe"),
    USER_ALREADY_EXISTS(Response.Status.BAD_REQUEST, "4", "El usuario ya existe"),
    INVALID_PARAMS(Response.Status.BAD_REQUEST, "5", "Parámetros inválidos");

    private final Response.Status status;
    private final String code;
    private final String message;

    ResponseCode(Response.Status status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status.getStatusCode());
        map.put("code", code);
        map.put("message", message);
        return map;
    }

    public Map<String, Object> asMap(Object result) {
        Map<String, Object> map = asMap();
        map.put("result", result);
        return map;
    }
}
