package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class TenantNotFoundException extends ApplicationRuntimeException {
    public TenantNotFoundException() {
        super("İşletme bulunamadı", ErrorMessageConstant.TENANT_NOT_FOUND);
    }
}
