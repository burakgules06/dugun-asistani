package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class LeadNotBelongToTenantException extends ApplicationRuntimeException {
    public LeadNotBelongToTenantException() {
        super("Talep bu işletmeye ait değil", ErrorMessageConstant.LEAD_NOT_BELONG_TO_TENANT);
    }
}
