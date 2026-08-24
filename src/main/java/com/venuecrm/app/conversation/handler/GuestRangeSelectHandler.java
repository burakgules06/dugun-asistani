package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.*;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.service.CapacityRangeService;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuestRangeSelectHandler implements StateHandler {

    private final WhatsAppClient whatsAppClient;
    private final ContextService contextService;
    private final BookingMessageService bookingMessages;
    private final CapacityRangeService capacityRangeService;

    @Override
    public ConversationState state() {
        return ConversationState.GUEST_RANGE_SELECT;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        if (input == null || !input.startsWith("GRANGE_")) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PLEASE_SELECT_GUEST_RANGE);
            return;
        }

        var selected = capacityRangeService.computeRanges(ctx.tenant().getId()).stream()
                .filter(o -> o.id().equals(input))
                .findFirst()
                .orElse(null);

        if (selected == null) {
            resetToIdle(ctx, MessageUtil.INVALID_SELECTION);
            return;
        }

        var data = contextService.read(ctx.conversation());
        data.setGuestCountMin(selected.start());
        data.setGuestCountMax(selected.end());

        if (selected.halls().size() == 1) {
            data.setHallId(selected.halls().get(0).getId());
            contextService.write(ctx.conversation(), data);
            bookingMessages.sendMonthList(ctx, 0);
            ctx.conversation().setState(ConversationState.MONTH_SELECT);
        } else {
            data.setHallId(null);
            contextService.write(ctx.conversation(), data);
            bookingMessages.sendHallList(ctx, 0);
            ctx.conversation().setState(ConversationState.HALL_SELECT);
        }
    }

    private void resetToIdle(ConversationContext ctx, String message) {
        whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), message);
        ctx.conversation().setState(ConversationState.IDLE);
    }
}
