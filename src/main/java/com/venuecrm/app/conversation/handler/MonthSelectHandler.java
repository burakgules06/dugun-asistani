package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.*;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class MonthSelectHandler implements StateHandler {

    private final WhatsAppClient whatsAppClient;
    private final ContextService contextService;
    private final BookingMessageService bookingMessages;

    @Override
    public ConversationState state() {
        return ConversationState.MONTH_SELECT;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        if (input != null && input.startsWith("MONTH_PAGE_")) {
            int page = parseInt(input.substring("MONTH_PAGE_".length()));
            if (page < 0) { resetToIdle(ctx, MessageUtil.INVALID_SELECTION); return; }
            bookingMessages.sendMonthList(ctx, page);
            return;
        }

        if (input == null || !input.startsWith("MONTH_")) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PLEASE_SELECT_MONTH);
            return;
        }

        YearMonth ym;
        try {
            ym = YearMonth.parse(input.substring("MONTH_".length()));
        } catch (DateTimeParseException e) {
            resetToIdle(ctx, MessageUtil.INVALID_MONTH);
            return;
        }

        var data = contextService.read(ctx.conversation());
        data.setPreferredMonth(ym.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        data.setPreferredWeek(null);
        contextService.write(ctx.conversation(), data);

        bookingMessages.sendWeekList(ctx);
        ctx.conversation().setState(ConversationState.WEEK_SELECT);
    }

    private void resetToIdle(ConversationContext ctx, String message) {
        whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), message);
        ctx.conversation().setState(ConversationState.IDLE);
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
}
