package com.venuecrm.app.service.impl;

import com.venuecrm.app.conversation.ConversationContext;
import com.venuecrm.app.model.entity.Customer;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.entity.WaConversation;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.repository.WaConversationRepository;
import com.venuecrm.app.service.ConversationService;
import com.venuecrm.app.service.CustomerService;
import com.venuecrm.app.service.InboundMessageService;
import com.venuecrm.app.service.MessageLogService;
import com.venuecrm.app.service.TenantService;
import com.venuecrm.app.webhook.dto.WebhookPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class InboundMessageServiceImpl implements InboundMessageService {

    private static final Logger log = LoggerFactory.getLogger(InboundMessageServiceImpl.class);

    private final TenantService tenantService;
    private final CustomerService customerService;
    private final MessageLogService messageLogService;
    private final ObjectMapper objectMapper;
    private final ConversationService conversationService;
    private final WaConversationRepository conversationRepository;

    @Override
    public void process(WebhookPayload payload) {
        if (payload.entry() == null) return;

        payload.entry().forEach(entry -> {
            if (entry.changes() == null) return;

            entry.changes().forEach(change -> {
                var value = change.value();
                if (value == null || value.messages() == null) return;

                String phoneNumberId = value.metadata().phoneNumberId();

                Tenant tenant = tenantService.findByPhoneNumberId(phoneNumberId).orElse(null);
                if (tenant == null) {
                    log.warn("Taninmayan phone_number_id: {} - mesaj yok sayildi", phoneNumberId);
                    return;
                }
                final String pName = handleProfileName(value);

                value.messages().forEach(msg -> {
                    try {
                        processMessage(tenant, phoneNumberId, pName, msg);
                    } catch (Exception e) {
                        log.error("Mesaj islenemedi (id={}): {}", msg.id(), e.getMessage(), e);
                    }
                });
            });
        });
    }

    private void processMessage(Tenant tenant, String phoneNumberId, String pName, WebhookPayload.Message msg) {
        if (messageLogService.alreadyProcessed(msg.id())) {
            log.info("Mukerrer mesaj atlandi: {}", msg.id());
            return;
        }

        Customer customer = customerService.findOrCreate(tenant, msg.from(), msg.fromUserId(), pName);
        messageLogService.logInbound(tenant, customer, msg.id(), toJson(msg));
        String userInput = extractUserInput(msg);
        if (userInput == null) {
            log.info("Islenemeyen mesaj tipi: {}", msg.type());
            return;
        }

        WaConversation conv = conversationRepository
                .findByTenantIdAndCustomerId(tenant.getId(), customer.getId())
                .orElseGet(() -> {
                    WaConversation c = new WaConversation();
                    c.setTenant(tenant);
                    c.setCustomer(customer);
                    c.setState(ConversationState.IDLE);
                    c.setContext("{}");
                    return c;
                });

        ConversationContext convCtx = new ConversationContext(tenant, customer, conv, phoneNumberId);
        conversationService.handleInput(convCtx, userInput);
        conv.setLastInboundAt(OffsetDateTime.now());
        conversationRepository.save(conv);
    }

    private String handleProfileName(WebhookPayload.Value value){
        String profileName = null;
        if (value.contacts() != null && !value.contacts().isEmpty()) {
            var contact = value.contacts().get(0);
            if (contact.profile() != null) profileName = contact.profile().name();
        }
        return profileName;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("JSON serialize edilemedi: {}", e.getMessage());
            return "{}";
        }
    }

    private String extractUserInput(WebhookPayload.Message msg) {
        if ("text".equals(msg.type()) && msg.text() != null) {
            return msg.text().body();
        }
        if ("interactive".equals(msg.type()) && msg.interactive() != null) {
            var interactive = msg.interactive();
            if (interactive.buttonReply() != null) {
                return interactive.buttonReply().id();
            }
            if (interactive.listReply() != null) {
                return interactive.listReply().id();
            }
        }
        return null;
    }
}
