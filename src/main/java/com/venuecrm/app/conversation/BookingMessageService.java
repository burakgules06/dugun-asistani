package com.venuecrm.app.conversation;

import com.venuecrm.app.model.enums.ConversationState;

public interface BookingMessageService {
    void sendGuestRangeList(ConversationContext ctx);
    void sendHallList(ConversationContext ctx, int page);
    void sendMenuList(ConversationContext ctx, int page);
    void sendMonthList(ConversationContext ctx, int page);
    void sendWeekList(ConversationContext ctx);
    void sendTimeSlotPrompt(ConversationContext ctx);
    void sendConfirmSummary(ConversationContext ctx);
    void renderState(ConversationContext ctx, ConversationState state, ConversationContextData data);
}
