package com.venuecrm.app.exception.runtime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationRuntimeException extends RuntimeException {

    private int error_code;

    public ApplicationRuntimeException(String message, int error_code) {
        super(message);
        this.error_code = error_code;
    }

    public ApplicationRuntimeException() {
    }
}
