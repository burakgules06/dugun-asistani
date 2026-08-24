package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.BookingMessageService;
import com.venuecrm.app.conversation.ContextService;
import com.venuecrm.app.conversation.ConversationContext;
import com.venuecrm.app.conversation.StateHandler;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Karşılama: ara menü göstermeden doğrudan kişi sayısı aralığı sorusuyla teklif akışını başlatır. */
@Component
@RequiredArgsConstructor
public class IdleHandler implements StateHandler {

    private final WhatsAppClient whatsAppClient;
    private final ContextService contextService;
    private final BookingMessageService bookingMessages;

    @Override
    public ConversationState state() {
        return ConversationState.IDLE;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        var tenant = ctx.tenant();
        if (tenant.getGreetingText() != null && !tenant.getGreetingText().isBlank()) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), tenant.getGreetingText());
        }

        var data = contextService.read(ctx.conversation());
        data.setGuestCountMin(null);
        data.setGuestCountMax(null);
        data.setHallId(null);
        data.setMenuId(null);
        data.setPreferredMonth(null);
        data.setPreferredWeek(null);
        data.setPreferredTimeSlot(null);
        contextService.write(ctx.conversation(), data);

        bookingMessages.sendGuestRangeList(ctx);
        ctx.conversation().setState(ConversationState.GUEST_RANGE_SELECT);
    }
}
