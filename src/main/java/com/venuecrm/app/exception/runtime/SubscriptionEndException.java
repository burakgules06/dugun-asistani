package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class SubscriptionEndException extends ApplicationRuntimeException {
    public SubscriptionEndException() {
        super("Abonelik süresi doldu", ErrorMessageConstant.SUBSCRIPTION_END);
    }
}
