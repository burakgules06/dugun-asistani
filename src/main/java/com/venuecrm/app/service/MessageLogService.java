package com.venuecrm.app.service;

import com.venuecrm.app.model.entity.Customer;
import com.venuecrm.app.model.entity.Tenant;

public interface MessageLogService {
    boolean alreadyProcessed(String waMessageId);
    void logInbound(Tenant tenant, Customer customer, String waMessageId, String rawPayload);
    void logOutbound(Tenant tenant, Customer customer, String rawPayload);
}
