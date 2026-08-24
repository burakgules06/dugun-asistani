package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class CapacityRuleNotBelongToTenantException extends ApplicationRuntimeException {
    public CapacityRuleNotBelongToTenantException() {
        super("Kapasite kuralı bu işletmeye ait değil", ErrorMessageConstant.CAPACITY_RULE_NOT_BELONG_TO_TENANT);
    }
}
