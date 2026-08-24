package com.venuecrm.app.exception.runtime;

import com.venuecrm.app.constant.ErrorMessageConstant;

public class PanelUserPhoneAlreadyExistException extends ApplicationRuntimeException {
    public PanelUserPhoneAlreadyExistException() {
        super("Bu telefon numarasıyla kayıtlı bir kullanıcı zaten var", ErrorMessageConstant.PANEL_USER_PHONE_ALREADY_EXIST);
    }
}
