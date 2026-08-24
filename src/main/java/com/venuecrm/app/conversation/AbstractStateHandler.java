package com.venuecrm.app.conversation;

import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.whatsapp.WhatsAppClient;

public abstract class AbstractStateHandler implements StateHandler {

    protected final WhatsAppClient whatsAppClient;

    protected AbstractStateHandler(WhatsAppClient whatsAppClient) {
        this.whatsAppClient = whatsAppClient;
    }

    protected void sendText(ConversationContext ctx, String message) {
        whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(),
                ctx.customer().toWaTarget(), message);
    }

    protected void resetToIdle(ConversationContext ctx, String message) {
        sendText(ctx, message);
        ctx.conversation().setState(ConversationState.IDLE);
    }

    protected java.util.UUID parseUuid(String s) {
        try {
            return java.util.UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }
}
