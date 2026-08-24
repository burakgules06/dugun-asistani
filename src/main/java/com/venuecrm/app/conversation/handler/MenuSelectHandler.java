package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.*;
import com.venuecrm.app.model.entity.Menu;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.repository.MenuRepository;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MenuSelectHandler implements StateHandler {

    private final WhatsAppClient whatsAppClient;
    private final MenuRepository menuRepository;
    private final ContextService contextService;
    private final BookingMessageService bookingMessages;

    @Override
    public ConversationState state() {
        return ConversationState.MENU_SELECT;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        if (input != null && input.startsWith("MENU_PAGE_")) {
            int page = parseInt(input.substring("MENU_PAGE_".length()));
            if (page < 0) { resetToIdle(ctx, MessageUtil.INVALID_SELECTION); return; }
            bookingMessages.sendMenuList(ctx, page);
            return;
        }

        if (input == null || !input.startsWith("MENU_")) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PLEASE_SELECT_MENU);
            return;
        }

        UUID menuId = parseUuid(input.substring("MENU_".length()));
        if (menuId == null) { resetToIdle(ctx, MessageUtil.INVALID_SELECTION); return; }

        Menu menu = menuRepository.findById(menuId).orElse(null);
        if (menu == null || !menu.getTenant().getId().equals(ctx.tenant().getId()) || !menu.isActive()) {
            resetToIdle(ctx, MessageUtil.MENU_NOT_FOUND);
            return;
        }

        var data = contextService.read(ctx.conversation());
        data.setMenuId(menuId);
        contextService.write(ctx.conversation(), data);

        bookingMessages.sendTimeSlotPrompt(ctx);
        ctx.conversation().setState(ConversationState.TIME_SLOT_SELECT);
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
