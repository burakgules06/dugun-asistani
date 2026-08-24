package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class HallNotFoundException extends ApplicationRuntimeException {
    public HallNotFoundException() {
        super("Salon bulunamadı", ErrorMessageConstant.HALL_NOT_FOUND);
    }
}
