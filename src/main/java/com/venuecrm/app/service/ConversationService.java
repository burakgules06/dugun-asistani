package com.venuecrm.app.service;

import com.venuecrm.app.conversation.ConversationContext;

public interface ConversationService {
    void handleInput(ConversationContext ctx, String input);
}
