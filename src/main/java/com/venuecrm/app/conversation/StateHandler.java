package com.venuecrm.app.conversation;

import com.venuecrm.app.model.enums.ConversationState;

public interface StateHandler {
    ConversationState state();
    void handle(ConversationContext ctx, String input);
}
