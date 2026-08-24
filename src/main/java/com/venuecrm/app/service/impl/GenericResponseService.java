package com.venuecrm.app.service.impl;

import com.venuecrm.app.model.GenericResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class GenericResponseService {

    public <T> GenericResponse<T> createResponse(boolean responseType, String message, T data, int errorCode) {
        return new GenericResponse<T>().setData(data).setSuccess(responseType).setMessage(message).setCode(errorCode)
                .setRequestId(MDC.get("requestId"));
    }

    public <T> GenericResponse<T> createResponseWithError(String message, T data, int errorCode) {
        return this.createResponse(false, message, data, errorCode);
    }

    public <T> GenericResponse<T> createResponseWithError(String message, int errorCode) {
        return this.createResponseWithError(message, null, errorCode);
    }
}
