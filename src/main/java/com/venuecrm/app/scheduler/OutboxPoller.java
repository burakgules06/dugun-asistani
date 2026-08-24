package com.venuecrm.app.scheduler;

import com.venuecrm.app.model.entity.MessageOutbox;
import com.venuecrm.app.model.enums.OutboxStatus;
import com.venuecrm.app.repository.MessageOutboxRepository;
import com.venuecrm.app.whatsapp.WaTarget;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;

/** Gecikmeli/toplu WhatsApp mesajları (takip hatırlatmaları vb.) için transactional outbox poller. */
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

    private final MessageOutboxRepository outboxRepository;
    private final WhatsAppClient whatsAppClient;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 600000)
    @Transactional
    public void pollAndSend() {
        List<MessageOutbox> due = outboxRepository
                .findByStatusAndScheduledForBefore(OutboxStatus.PENDING, OffsetDateTime.now());

        if (due.isEmpty()) return;

        log.info("Gönderilecek {} outbox mesajı bulundu", due.size());

        for (MessageOutbox msg : due) {
            try {
                JsonNode payload = objectMapper.readTree(msg.getPayload());
                String phone = payload.path("to").asText(null);
                String bsuid = payload.path("bsuid").asText(null);
                WaTarget to = new WaTarget(phone, bsuid);
                String phoneId = payload.get("phoneNumberId").asText();
                String accessToken = payload.get("accessToken").asText();
                String text = payload.path("text").asText("");

                whatsAppClient.sendText(accessToken, phoneId, to, text);

                msg.setStatus(OutboxStatus.SENT);
                msg.setSentAt(OffsetDateTime.now());
                log.info("Outbox mesajı gönderildi: {}", msg.getId());

            } catch (Exception e) {
                msg.setAttemptCount((short) (msg.getAttemptCount() + 1));
                msg.setLastError(e.getMessage());
                if (msg.getAttemptCount() >= 3) {
                    msg.setStatus(OutboxStatus.DEAD);
                    log.warn("Outbox mesajı DEAD (3 deneme): {}", msg.getId());
                } else {
                    log.warn("Outbox mesajı gönderilemedi (deneme {}): {}", msg.getAttemptCount(), e.getMessage());
                }
            }
        }
        outboxRepository.saveAll(due);
    }
}
