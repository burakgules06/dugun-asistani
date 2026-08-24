package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class CustomerNumberAlreadyExistException extends ApplicationRuntimeException {
    public CustomerNumberAlreadyExistException() {
        super("Bu telefon numarası zaten kayıtlı", ErrorMessageConstant.CUSTOMER_NUMBER_ALREADY_EXIST);
    }
}
