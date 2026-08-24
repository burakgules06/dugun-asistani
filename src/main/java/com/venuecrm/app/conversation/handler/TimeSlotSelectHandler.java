package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.*;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.model.enums.TimeSlot;
import com.venuecrm.app.service.AvailabilityEvaluationService;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
public class TimeSlotSelectHandler implements StateHandler {

    private final WhatsAppClient whatsAppClient;
    private final ContextService contextService;
    private final BookingMessageService bookingMessages;
    private final AvailabilityEvaluationService availabilityEvaluationService;

    @Override
    public ConversationState state() {
        return ConversationState.TIME_SLOT_SELECT;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        TimeSlot slot = switch (input == null ? "" : input) {
            case "TSLOT_WEEKDAY_EVENING" -> TimeSlot.WEEKDAY_EVENING;
            case "TSLOT_WEEKEND_EVENING" -> TimeSlot.WEEKEND_EVENING;
            case "TSLOT_WEEKEND_DAY" -> TimeSlot.WEEKEND_DAY;
            default -> null;
        };

        if (slot == null) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PLEASE_SELECT_TIME_SLOT);
            return;
        }

        var data = contextService.read(ctx.conversation());
        Integer month = data.getPreferredMonth() != null
                ? YearMonth.from(LocalDate.parse(data.getPreferredMonth())).getMonthValue() : null;

        boolean closed = availabilityEvaluationService.isClosed(
                ctx.tenant().getId(), data.getHallId(), data.getMenuId(), month, slot);
        if (closed) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PLEASE_SELECT_TIME_SLOT);
            return;
        }

        data.setPreferredTimeSlot(slot.name());
        contextService.write(ctx.conversation(), data);

        bookingMessages.sendConfirmSummary(ctx);
        ctx.conversation().setState(ConversationState.CONFIRM);
    }
}
