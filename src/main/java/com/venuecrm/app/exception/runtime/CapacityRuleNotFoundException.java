package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class CapacityRuleNotFoundException extends ApplicationRuntimeException {
    public CapacityRuleNotFoundException() {
        super("Kapasite kuralı bulunamadı", ErrorMessageConstant.CAPACITY_RULE_NOT_FOUND);
    }
}
