package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class LeadNotFoundException extends ApplicationRuntimeException {
    public LeadNotFoundException() {
        super("Talep bulunamadı", ErrorMessageConstant.LEAD_NOT_FOUND);
    }
}
