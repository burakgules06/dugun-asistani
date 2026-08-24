package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class MenuNotBelongToTenantException extends ApplicationRuntimeException {
    public MenuNotBelongToTenantException() {
        super("Menü bu işletmeye ait değil", ErrorMessageConstant.MENU_NOT_BELONG_TO_TENANT);
    }
}
