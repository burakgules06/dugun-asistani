package com.venuecrm.app.service.impl;

import com.venuecrm.app.model.entity.Customer;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.entity.WaMessageLog;
import com.venuecrm.app.repository.WaMessageLogRepository;
import com.venuecrm.app.service.MessageLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageLogServiceImpl implements MessageLogService {

    private final WaMessageLogRepository logRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean alreadyProcessed(String waMessageId) {
        if (waMessageId == null) return false;
        return logRepository.existsByWaMessageId(waMessageId);
    }

    @Override
    @Transactional
    public void logInbound(Tenant tenant, Customer customer, String waMessageId, String rawPayload) {
        WaMessageLog log = new WaMessageLog();
        log.setTenant(tenant);
        log.setCustomer(customer);
        log.setDirection("IN");
        log.setWaMessageId(waMessageId);
        log.setPayload(rawPayload);
        logRepository.save(log);
    }

    @Override
    @Transactional
    public void logOutbound(Tenant tenant, Customer customer, String rawPayload) {
        WaMessageLog log = new WaMessageLog();
        log.setTenant(tenant);
        log.setCustomer(customer);
        log.setDirection("OUT");
        log.setPayload(rawPayload);
        logRepository.save(log);
    }
}
