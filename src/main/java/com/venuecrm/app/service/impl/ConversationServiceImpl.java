package com.venuecrm.app.service.impl;

import com.venuecrm.app.conversation.*;
import com.venuecrm.app.model.entity.WaConversation;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.service.ConversationService;
import com.venuecrm.app.service.TenantService;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final Map<ConversationState, StateHandler> handlers;
    private static final Locale TR = new Locale("tr", "TR");
    private static final Duration CONVERSATION_TIMEOUT = Duration.ofMinutes(30);
    private final Clock clock;
    private final WhatsAppClient whatsAppClient;
    private final TenantService tenantService;
    private final ContextService contextService;
    private final BookingMessageService bookingMessageService;
    private static final Set<String> RESTART_KEYWORDS = Set.of(
            "baştan", "bastan", "başla", "basla",
            "menü", "menu", "iptal", "merhaba", "selam"
    );
    private static final Set<String> BACK_KEYWORDS = Set.of(
            "geri", "geri git", "geri dön", "geri don", "önceki", "onceki"
    );
    private static final Set<ConversationState> RENDERABLE_STATES = Set.of(
            ConversationState.GUEST_RANGE_SELECT,
            ConversationState.HALL_SELECT,
            ConversationState.MENU_SELECT,
            ConversationState.MONTH_SELECT,
            ConversationState.WEEK_SELECT,
            ConversationState.TIME_SLOT_SELECT,
            ConversationState.CONFIRM
    );

    public ConversationServiceImpl(List<StateHandler> handlerList, Clock clock,
                                   WhatsAppClient whatsAppClient, TenantService tenantService,
                                   ContextService contextService, BookingMessageService bookingMessageService) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(StateHandler::state, h -> h));
        this.clock = clock;
        this.whatsAppClient = whatsAppClient;
        this.tenantService = tenantService;
        this.contextService = contextService;
        this.bookingMessageService = bookingMessageService;
    }

    @Override
    @Transactional
    public void handleInput(ConversationContext ctx, String input) {
        WaConversation conv = ctx.conversation();

        if (!tenantService.isSubscriptionActive(ctx.tenant())) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(),
                    ctx.customer().toWaTarget(), MessageUtil.SUBSCRIPTION_INACTIVE);
            return;
        }

        if (ctx.customer().getFullName() == null) {
            if (conv.getState() != ConversationState.NAME_INPUT) {
                conv.setState(ConversationState.NAME_INPUT);
                whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(),
                        MessageUtil.ASK_NAME_PROMPT);
                return;
            }
        }

        if (isStale(conv)) {
            conv.setState(ConversationState.IDLE);
            conv.setContext("{}");
        }
        if (isRestartCommand(input)) {
            conv.setState(ConversationState.IDLE);
            conv.setContext("{}");
        }

        if (conv.getState() != ConversationState.IDLE
                && conv.getState() != ConversationState.NAME_INPUT
                && isBackCommand(input)) {
            handleBack(ctx);
            return;
        }

        ConversationState before = conv.getState();
        StateHandler handler = handlers.get(before);
        if (handler == null) {
            handler = handlers.get(ConversationState.IDLE);
        }
        handler.handle(ctx, input);

        recordHistory(ctx, before);
    }

    private void handleBack(ConversationContext ctx) {
        WaConversation conv = ctx.conversation();
        var data = contextService.read(conv);
        ConversationState target = data.popHistory();
        if (target == null) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(),
                    MessageUtil.NO_BACK_STEP);
            return;
        }
        contextService.write(conv, data);
        bookingMessageService.renderState(ctx, target, data);
    }

    private void recordHistory(ConversationContext ctx, ConversationState before) {
        WaConversation conv = ctx.conversation();
        ConversationState after = conv.getState();

        if (after == ConversationState.IDLE) {
            var data = contextService.read(conv);
            if (data.hasHistory()) {
                data.setHistory(new java.util.ArrayList<>());
                contextService.write(conv, data);
            }
            return;
        }

        if (before != after && before != ConversationState.IDLE && RENDERABLE_STATES.contains(before)) {
            var data = contextService.read(conv);
            data.pushHistory(before);
            contextService.write(conv, data);
        }
    }

    private boolean isStale(WaConversation conv) {
        if (conv.getState() == ConversationState.IDLE) return false;
        if (conv.getLastInboundAt() == null) return false;
        OffsetDateTime cutoff = OffsetDateTime.now(clock).minus(CONVERSATION_TIMEOUT);
        return conv.getLastInboundAt().isBefore(cutoff);
    }

    private boolean isRestartCommand(String input) {
        if (input == null) return false;
        return RESTART_KEYWORDS.contains(input.trim().toLowerCase(TR));
    }

    private boolean isBackCommand(String input) {
        if (input == null) return false;
        return BACK_KEYWORDS.contains(input.trim().toLowerCase(TR));
    }
}
