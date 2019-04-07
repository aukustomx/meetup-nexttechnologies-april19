package com.aukustomx.simpleapp.infra.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

import static com.aukustomx.simpleapp.common.model.ResponseCode.FAILED_OPERATION;
import static com.aukustomx.simpleapp.common.model.ResponseCode.INVALID_PARAMS;

@Provider
public class UserExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {

        System.out.println(e.getClass());

        //Exceptions generadas por el negocio
        if (e instanceof UserException) {
            return responseOf((UserException) e);
        }

        if (e instanceof ConstraintViolationException) {
            return responseOf((ConstraintViolationException) e);
        }

        //Cualquier otra exception
        return responseOf(e);
    }

    private static Response responseOf(UserException e) {
        return Response
                .status(e.getResponseCode().getStatus())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(e.getResponseCode().asMap())
                .build();
    }

    private static Response responseOf(ConstraintViolationException e) {
        List<String> errorMessages = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return Response
                .status(INVALID_PARAMS.getStatus())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(INVALID_PARAMS.asMap(errorMessages))
                .build();
    }

    private static Response responseOf(Exception e) {
        return Response
                .status(FAILED_OPERATION.getStatus())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(FAILED_OPERATION.asMap())
                .build();
    }


}
