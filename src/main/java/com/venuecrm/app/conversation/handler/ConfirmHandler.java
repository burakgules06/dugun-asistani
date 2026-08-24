package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.*;
import com.venuecrm.app.model.entity.Hall;
import com.venuecrm.app.model.entity.Menu;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.model.enums.TimeSlot;
import com.venuecrm.app.repository.HallRepository;
import com.venuecrm.app.repository.MenuRepository;
import com.venuecrm.app.service.LeadService;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ConfirmHandler implements StateHandler {

    private static final Logger log = LoggerFactory.getLogger(ConfirmHandler.class);

    private final WhatsAppClient whatsAppClient;
    private final HallRepository hallRepository;
    private final MenuRepository menuRepository;
    private final LeadService leadService;
    private final ContextService contextService;

    @Override
    public ConversationState state() {
        return ConversationState.CONFIRM;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        if ("CONFIRM_NO".equals(input)) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.REQUEST_CANCELLED);
            clearAndIdle(ctx);
            return;
        }
        if (!"CONFIRM_YES".equals(input)) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PLEASE_CONFIRM_OR_CANCEL);
            return;
        }

        var data = contextService.read(ctx.conversation());
        Hall hall = data.getHallId() != null ? hallRepository.findById(data.getHallId()).orElse(null) : null;
        Menu menu = data.getMenuId() != null ? menuRepository.findById(data.getMenuId()).orElse(null) : null;
        LocalDate preferredMonth = data.getPreferredMonth() != null ? LocalDate.parse(data.getPreferredMonth()) : null;
        TimeSlot preferredTimeSlot = data.getPreferredTimeSlot() != null ? TimeSlot.valueOf(data.getPreferredTimeSlot()) : null;

        leadService.createFromBot(ctx.tenant(), ctx.customer(), hall, menu,
                preferredMonth, data.getPreferredWeek(), preferredTimeSlot,
                data.getGuestCountMin(), data.getGuestCountMax());

        log.info("[{}] TALEP: {} - {} - {} (hafta {}, {}) - {}-{} kişi",
                ctx.tenant().getSlug(), ctx.customer().getFullName(),
                hall != null ? hall.getName() : "-", preferredMonth, data.getPreferredWeek(), preferredTimeSlot,
                data.getGuestCountMin(), data.getGuestCountMax());

        whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.REQUEST_RECEIVED_TEMPLATE);
        clearAndIdle(ctx);
    }

    private void clearAndIdle(ConversationContext ctx) {
        ctx.conversation().setContext("{}");
        ctx.conversation().setState(ConversationState.IDLE);
    }
}
