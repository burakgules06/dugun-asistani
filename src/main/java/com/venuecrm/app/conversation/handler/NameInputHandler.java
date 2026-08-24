package com.venuecrm.app.conversation.handler;

import com.venuecrm.app.conversation.ConversationContext;
import com.venuecrm.app.conversation.StateHandler;
import com.venuecrm.app.model.entity.Customer;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.repository.CustomerRepository;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NameInputHandler implements StateHandler {

    private final WhatsAppClient whatsAppClient;
    private final CustomerRepository customerRepository;
    private final IdleHandler idleHandler;

    @Override
    public ConversationState state() {
        return ConversationState.NAME_INPUT;
    }

    @Override
    public void handle(ConversationContext ctx, String input) {
        Customer customer = ctx.customer();

        String name = MessageUtil.cleanName(input);
        if (name == null) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), customer.toWaTarget(),
                    MessageUtil.ASK_NAME_INVALID);
            return;
        }

        customer.setFullName(name);
        customerRepository.save(customer);

        whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), customer.toWaTarget(),
                String.format(MessageUtil.NAME_SAVED_TEMPLATE, name));

        ctx.conversation().setState(ConversationState.IDLE);
        idleHandler.handle(ctx, null);
    }
}
