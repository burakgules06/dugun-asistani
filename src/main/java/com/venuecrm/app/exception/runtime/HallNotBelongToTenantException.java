package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class HallNotBelongToTenantException extends ApplicationRuntimeException {
    public HallNotBelongToTenantException() {
        super("Salon bu işletmeye ait değil", ErrorMessageConstant.HALL_NOT_BELONG_TO_TENANT);
    }
}
