package com.venuecrm.app.conversation;

import com.venuecrm.app.model.entity.Customer;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.entity.WaConversation;

public record ConversationContext(
        Tenant tenant, Customer customer, WaConversation conversation, String phoneNumberId
) {
    public String accessToken() {
        return tenant.getWaAccessToken();
    }
}
