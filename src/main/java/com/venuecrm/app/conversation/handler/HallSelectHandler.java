package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.*;
import com.venuecrm.app.model.entity.Hall;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.repository.HallRepository;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HallSelectHandler implements StateHandler {

    private final WhatsAppClient whatsAppClient;
    private final HallRepository hallRepository;
    private final ContextService contextService;
    private final BookingMessageService bookingMessages;

    @Override
    public ConversationState state() {
        return ConversationState.HALL_SELECT;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        if (input != null && input.startsWith("HALL_PAGE_")) {
            int page = parseInt(input.substring("HALL_PAGE_".length()));
            if (page < 0) { resetToIdle(ctx, MessageUtil.INVALID_SELECTION); return; }
            bookingMessages.sendHallList(ctx, page);
            return;
        }

        if (input == null || !input.startsWith("HALL_")) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PLEASE_SELECT_HALL);
            return;
        }

        UUID hallId = parseUuid(input.substring("HALL_".length()));
        if (hallId == null) { resetToIdle(ctx, MessageUtil.INVALID_SELECTION); return; }

        Hall hall = hallRepository.findById(hallId).orElse(null);
        if (hall == null || !hall.getTenant().getId().equals(ctx.tenant().getId()) || !hall.isActive()) {
            resetToIdle(ctx, MessageUtil.HALL_NOT_FOUND);
            return;
        }

        var data = contextService.read(ctx.conversation());
        data.setHallId(hallId);
        contextService.write(ctx.conversation(), data);

        bookingMessages.sendMonthList(ctx, 0);
        ctx.conversation().setState(ConversationState.MONTH_SELECT);
    }

    private void resetToIdle(ConversationContext ctx, String message) {
        whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), message);
        ctx.conversation().setState(ConversationState.IDLE);
    }

    private UUID parseUuid(String s) {
        try { return UUID.fromString(s); } catch (Exception e) { return null; }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
}
