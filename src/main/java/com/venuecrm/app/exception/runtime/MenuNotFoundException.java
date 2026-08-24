package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class MenuNotFoundException extends ApplicationRuntimeException {
    public MenuNotFoundException() {
        super("Menü bulunamadı", ErrorMessageConstant.MENU_NOT_FOUND);
    }
}
