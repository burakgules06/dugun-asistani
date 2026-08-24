package com.venuecrm.app.conversation;

import com.venuecrm.app.model.entity.WaConversation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ContextService {

    private final ObjectMapper objectMapper;

    public ConversationContextData read(WaConversation conv) {
        try {
            String json = conv.getContext();
            if (json == null || json.isBlank()) return new ConversationContextData();
            return objectMapper.readValue(json, ConversationContextData.class);
        } catch (Exception e) {
            return new ConversationContextData();
        }
    }

    public void write(WaConversation conv, ConversationContextData data) {
        try {
            conv.setContext(objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            conv.setContext("{}");
        }
    }
}
