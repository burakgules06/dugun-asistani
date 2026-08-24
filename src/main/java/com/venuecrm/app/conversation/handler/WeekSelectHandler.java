package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.*;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.util.DateUtil;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
public class WeekSelectHandler implements StateHandler {

    private final WhatsAppClient whatsAppClient;
    private final ContextService contextService;
    private final BookingMessageService bookingMessages;

    @Override
    public ConversationState state() {
        return ConversationState.WEEK_SELECT;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        var data = contextService.read(ctx.conversation());
        if (data.getPreferredMonth() == null) {
            resetToIdle(ctx, MessageUtil.PROBLEM_OCCURED);
            return;
        }

        Integer week = parseWeek(input);
        YearMonth ym = YearMonth.from(LocalDate.parse(data.getPreferredMonth()));
        int weekCount = DateUtil.weekCountInMonth(ym);

        if (week == null || week < 1 || week > weekCount) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PLEASE_SELECT_WEEK);
            return;
        }

        data.setPreferredWeek(week);
        contextService.write(ctx.conversation(), data);

        bookingMessages.sendMenuList(ctx, 0);
        ctx.conversation().setState(ConversationState.MENU_SELECT);
    }

    private Integer parseWeek(String input) {
        if (input == null || !input.startsWith("WEEK_")) return null;
        try {
            return Integer.parseInt(input.substring("WEEK_".length()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void resetToIdle(ConversationContext ctx, String message) {
        whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), message);
        ctx.conversation().setState(ConversationState.IDLE);
    }
}
