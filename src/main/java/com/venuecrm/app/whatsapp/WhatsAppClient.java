package com.venuecrm.app.whatsapp;

import com.venuecrm.app.config.WhatsAppProperties;
import com.venuecrm.app.util.PhoneUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WhatsAppClient {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppClient.class);
    private final RestClient rest;

    public WhatsAppClient(WhatsAppProperties props) {
        this.rest = RestClient.builder()
                .baseUrl(props.api().baseUrl())
                .build();
    }

    public void sendText(String accessToken, String phoneNumberId, WaTarget to, String body) {
        Map<String, Object> payload = basePayload(to);
        payload.put("type", "text");
        payload.put("text", Map.of("body", body));
        send(accessToken, phoneNumberId, to, payload, "Mesaj");
    }

    /** Meta max 3 buton destekler. */
    public void sendButtons(String accessToken, String phoneNumberId, WaTarget to,
                            String bodyText, List<Button> buttons) {

        List<Map<String, Object>> buttonObjects = buttons.stream()
                .map(b -> Map.<String, Object>of(
                        "type", "reply",
                        "reply", Map.of("id", b.id(), "title", b.title())
                ))
                .toList();

        Map<String, Object> payload = basePayload(to);
        payload.put("type", "interactive");
        payload.put("interactive", Map.of(
                "type", "button",
                "body", Map.of("text", bodyText),
                "action", Map.of("buttons", buttonObjects)
        ));
        send(accessToken, phoneNumberId, to, payload, "Butonlu mesaj");
    }

    /** Liste mesaji (interactive list). 10 satira kadar destekler. */
    public void sendList(String accessToken, String phoneNumberId, WaTarget to,
                         String bodyText, String buttonText, List<Row> rows) {

        List<Map<String, Object>> rowObjects = rows.stream()
                .map(r -> {
                    if (r.description() != null && !r.description().isBlank()) {
                        return Map.<String, Object>of(
                                "id", r.id(), "title", r.title(), "description", r.description());
                    }
                    return Map.<String, Object>of("id", r.id(), "title", r.title());
                })
                .toList();

        Map<String, Object> section = Map.of("title", "Seçenekler", "rows", rowObjects);

        Map<String, Object> payload = basePayload(to);
        payload.put("type", "interactive");
        payload.put("interactive", Map.of(
                "type", "list",
                "body", Map.of("text", bodyText),
                "action", Map.of(
                        "button", buttonText,
                        "sections", List.of(section)
                )
        ));
        send(accessToken, phoneNumberId, to, payload, "Liste mesaji");
    }

    private Map<String, Object> basePayload(WaTarget to) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        if (to.hasPhone()) {
            payload.put("to", to.phone());
        } else {
            payload.put("recipient", to.bsuid());
        }
        return payload;
    }

    private void send(String accessToken, String phoneNumberId, WaTarget to,
                      Map<String, Object> payload, String label) {
        String maskedTo = to.hasPhone() ? PhoneUtil.mask(to.phone()) : PhoneUtil.mask(to.bsuid());
        if (accessToken == null || accessToken.isBlank()) {
            log.error("{} gonderilemedi -> {} | tenant icin access token tanimli degil", label, maskedTo);
            return;
        }
        try {
            String response = rest.post()
                    .uri("/{phoneNumberId}/messages", phoneNumberId)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);
            log.info("{} gonderildi -> {} | yanit: {}", label, maskedTo, PhoneUtil.maskDigitRuns(response));
        } catch (Exception e) {
            log.error("{} gonderilemedi -> {} | hata: {}", label, maskedTo, e.getMessage());
        }
    }

    public record Row(String id, String title, String description) {}
    public record Button(String id, String title) {}
}
