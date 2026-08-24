package com.venuecrm.app.conversation;

import com.venuecrm.app.model.entity.Hall;
import com.venuecrm.app.model.entity.Menu;
import com.venuecrm.app.model.enums.ConversationState;
import com.venuecrm.app.model.enums.TimeSlot;
import com.venuecrm.app.repository.HallMenuRepository;
import com.venuecrm.app.repository.HallRepository;
import com.venuecrm.app.repository.MenuRepository;
import com.venuecrm.app.service.AvailabilityEvaluationService;
import com.venuecrm.app.service.CapacityRangeService;
import com.venuecrm.app.service.CapacityRangeService.GuestRangeOption;
import com.venuecrm.app.util.DateUtil;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.whatsapp.WaTarget;
import com.venuecrm.app.whatsapp.WhatsAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingMessageServiceImpl implements BookingMessageService {

    private static final int PAGE_SIZE = 9;
    private static final int MONTHS_AHEAD = 12;

    private final WhatsAppClient whatsAppClient;
    private final HallRepository hallRepository;
    private final MenuRepository menuRepository;
    private final HallMenuRepository hallMenuRepository;
    private final ContextService contextService;
    private final CapacityRangeService capacityRangeService;
    private final AvailabilityEvaluationService availabilityEvaluationService;

    @Override
    public void sendGuestRangeList(ConversationContext ctx) {
        WaTarget to = ctx.customer().toWaTarget();
        var options = capacityRangeService.computeRanges(ctx.tenant().getId());

        if (options.isEmpty()) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), to, MessageUtil.NO_HALL_DEFINED);
            ctx.conversation().setState(ConversationState.IDLE);
            return;
        }

        List<WhatsAppClient.Row> rows = options.stream()
                .map(o -> new WhatsAppClient.Row(o.id(), rangeLabel(o), null))
                .toList();

        whatsAppClient.sendList(ctx.accessToken(), ctx.phoneNumberId(), to,
                MessageUtil.SELECT_GUEST_RANGE_PROMPT, MessageUtil.LIST_BTN_GUEST_RANGE, rows);
    }

    @Override
    public void sendHallList(ConversationContext ctx, int page) {
        WaTarget to = ctx.customer().toWaTarget();
        var data = contextService.read(ctx.conversation());

        List<Hall> halls = matchingHalls(ctx, data);
        if (halls == null) {
            fallbackToIdle(ctx);
            return;
        }
        if (halls.isEmpty()) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), to, MessageUtil.NO_HALL_DEFINED);
            ctx.conversation().setState(ConversationState.IDLE);
            return;
        }

        int start = page * PAGE_SIZE;
        boolean hasMore = halls.size() > start + PAGE_SIZE;

        List<WhatsAppClient.Row> rows = halls.stream()
                .skip(start)
                .limit(PAGE_SIZE)
                .map(h -> new WhatsAppClient.Row("HALL_" + h.getId(), h.getName(), capacityLabel(h)))
                .collect(Collectors.toCollection(ArrayList::new));

        if (hasMore) {
            rows.add(new WhatsAppClient.Row("HALL_PAGE_" + (page + 1), "➕ Diğer Salonlar", null));
        }

        whatsAppClient.sendList(ctx.accessToken(), ctx.phoneNumberId(), to,
                MessageUtil.SELECT_HALL_PROMPT, MessageUtil.LIST_BTN_HALL, rows);
    }

    /** Musterinin secmis oldugu kisi araligina (context) uyan salonlari doner; context bozuksa null. */
    private List<Hall> matchingHalls(ConversationContext ctx, ConversationContextData data) {
        if (data.getGuestCountMin() == null) return null;
        return capacityRangeService.computeRanges(ctx.tenant().getId()).stream()
                .filter(o -> o.start() == data.getGuestCountMin()
                        && java.util.Objects.equals(o.end(), data.getGuestCountMax()))
                .findFirst()
                .map(GuestRangeOption::halls)
                .orElse(List.of());
    }

    @Override
    public void sendMenuList(ConversationContext ctx, int page) {
        WaTarget to = ctx.customer().toWaTarget();
        var data = contextService.read(ctx.conversation());
        Integer month = monthOf(data);

        var allMenus = menuRepository.findByTenantIdAndActiveTrueOrderBySortOrderAscNameAsc(ctx.tenant().getId());
        var menus = allMenus.stream()
                .filter(m -> isMenuAvailableForHall(m, data.getHallId()))
                .filter(m -> !availabilityEvaluationService.isClosed(
                        ctx.tenant().getId(), data.getHallId(), m.getId(), month, null))
                .toList();

        if (allMenus.isEmpty()) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), to, MessageUtil.NO_MENU_DEFINED);
            ctx.conversation().setState(ConversationState.IDLE);
            return;
        }
        if (menus.isEmpty()) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), to, MessageUtil.NO_MENU_AVAILABLE_FOR_PERIOD);
            ctx.conversation().setState(ConversationState.IDLE);
            return;
        }

        int start = page * PAGE_SIZE;
        boolean hasMore = menus.size() > start + PAGE_SIZE;

        List<WhatsAppClient.Row> rows = menus.stream()
                .skip(start)
                .limit(PAGE_SIZE)
                .map(m -> new WhatsAppClient.Row("MENU_" + m.getId(), m.getName(), priceLabel(ctx, m)))
                .collect(Collectors.toCollection(ArrayList::new));

        if (hasMore) {
            rows.add(new WhatsAppClient.Row("MENU_PAGE_" + (page + 1), "➕ Diğer Menüler", null));
        }

        whatsAppClient.sendList(ctx.accessToken(), ctx.phoneNumberId(), to,
                MessageUtil.SELECT_MENU_PROMPT, MessageUtil.LIST_BTN_MENU, rows);
    }

    @Override
    public void sendMonthList(ConversationContext ctx, int page) {
        LocalDate today = LocalDate.now();
        YearMonth rangeStart = YearMonth.from(today);

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, MONTHS_AHEAD);
        boolean hasMore = end < MONTHS_AHEAD;

        List<WhatsAppClient.Row> rows = new ArrayList<>();
        for (int i = start; i < end; i++) {
            YearMonth ym = rangeStart.plusMonths(i);
            rows.add(new WhatsAppClient.Row("MONTH_" + ym, DateUtil.monthLabel(ym), null));
        }
        if (hasMore) {
            rows.add(new WhatsAppClient.Row("MONTH_PAGE_" + (page + 1), "➕ Diğer Aylar", null));
        }

        whatsAppClient.sendList(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(),
                MessageUtil.SELECT_MONTH_PROMPT, MessageUtil.LIST_BTN_MONTH, rows);
    }

    @Override
    public void sendWeekList(ConversationContext ctx) {
        var data = contextService.read(ctx.conversation());
        if (data.getPreferredMonth() == null) {
            fallbackToIdle(ctx);
            return;
        }
        YearMonth ym = YearMonth.from(LocalDate.parse(data.getPreferredMonth()));
        int weekCount = DateUtil.weekCountInMonth(ym);

        List<WhatsAppClient.Row> rows = new ArrayList<>();
        for (int week = 1; week <= weekCount; week++) {
            rows.add(new WhatsAppClient.Row("WEEK_" + week,
                    String.format(MessageUtil.WEEK_LABEL_TEMPLATE, week),
                    DateUtil.weekRangeLabel(ym, week)));
        }

        whatsAppClient.sendList(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(),
                MessageUtil.SELECT_WEEK_PROMPT, MessageUtil.LIST_BTN_WEEK, rows);
    }

    @Override
    public void sendTimeSlotPrompt(ConversationContext ctx) {
        var data = contextService.read(ctx.conversation());
        Integer month = monthOf(data);
        Hall hall = data.getHallId() != null ? hallRepository.findById(data.getHallId()).orElse(null) : null;

        List<TimeSlot> candidates = new ArrayList<>(List.of(TimeSlot.WEEKDAY_EVENING, TimeSlot.WEEKEND_EVENING));
        if (hall != null && hall.getDailyCapacity() == 2) {
            candidates.add(TimeSlot.WEEKEND_DAY);
        }

        List<TimeSlot> open = candidates.stream()
                .filter(slot -> !availabilityEvaluationService.isClosed(
                        ctx.tenant().getId(), data.getHallId(), data.getMenuId(), month, slot))
                .toList();

        if (open.isEmpty()) {
            whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.NO_TIME_SLOT_AVAILABLE);
            ctx.conversation().setState(ConversationState.IDLE);
            return;
        }

        List<WhatsAppClient.Button> buttons = open.stream()
                .map(slot -> new WhatsAppClient.Button("TSLOT_" + slot.name(), timeSlotButtonLabel(slot)))
                .toList();

        whatsAppClient.sendButtons(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(),
                MessageUtil.SELECT_TIME_SLOT_PROMPT, buttons);
    }

    @Override
    public void sendConfirmSummary(ConversationContext ctx) {
        var data = contextService.read(ctx.conversation());
        Hall hall = data.getHallId() != null ? hallRepository.findById(data.getHallId()).orElse(null) : null;
        Menu menu = data.getMenuId() != null ? menuRepository.findById(data.getMenuId()).orElse(null) : null;

        String summary = String.format(MessageUtil.CONFIRM_SUMMARY_TEMPLATE,
                hall != null ? hall.getName() : "-",
                menu != null ? menu.getName() : "-",
                periodLabel(data),
                guestRangeLabel(data));

        if (data.getPreferredTimeSlot() != null) {
            summary += "\n🕐 " + timeSlotLabel(TimeSlot.valueOf(data.getPreferredTimeSlot()));
        }

        whatsAppClient.sendButtons(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(),
                summary,
                List.of(
                        new WhatsAppClient.Button("CONFIRM_YES", MessageUtil.BTN_CONFIRM_YES),
                        new WhatsAppClient.Button("CONFIRM_NO", MessageUtil.BTN_CONFIRM_NO)
                ));
    }

    /** "Ağustos 2027 · 2. Hafta (8-14 Ağustos)" biciminde ozet metni uretir. */
    private String periodLabel(ConversationContextData data) {
        if (data.getPreferredMonth() == null) return "-";
        YearMonth ym = YearMonth.from(LocalDate.parse(data.getPreferredMonth()));
        StringBuilder sb = new StringBuilder(DateUtil.monthLabel(ym));
        if (data.getPreferredWeek() != null) {
            sb.append(" · ").append(String.format(MessageUtil.WEEK_LABEL_TEMPLATE, data.getPreferredWeek()))
                    .append(" (").append(DateUtil.weekRangeLabel(ym, data.getPreferredWeek())).append(")");
        }
        return sb.toString();
    }

    private String guestRangeLabel(ConversationContextData data) {
        if (data.getGuestCountMin() == null) return "-";
        return data.getGuestCountMax() != null
                ? data.getGuestCountMin() + "-" + data.getGuestCountMax()
                : data.getGuestCountMin() + "+";
    }

    private String timeSlotLabel(TimeSlot slot) {
        return switch (slot) {
            case WEEKDAY_EVENING -> "Hafta İçi Akşam";
            case WEEKEND_EVENING -> "Hafta Sonu Akşam";
            case WEEKEND_DAY -> "Hafta Sonu Gündüz";
        };
    }

    private String timeSlotButtonLabel(TimeSlot slot) {
        return switch (slot) {
            case WEEKDAY_EVENING -> MessageUtil.BTN_TIME_SLOT_WEEKDAY_EVENING;
            case WEEKEND_EVENING -> MessageUtil.BTN_TIME_SLOT_WEEKEND_EVENING;
            case WEEKEND_DAY -> MessageUtil.BTN_TIME_SLOT_WEEKEND_DAY;
        };
    }

    private Integer monthOf(ConversationContextData data) {
        return data.getPreferredMonth() != null
                ? YearMonth.from(LocalDate.parse(data.getPreferredMonth())).getMonthValue() : null;
    }

    private String rangeLabel(GuestRangeOption o) {
        return (o.end() != null ? o.start() + "-" + o.end() : o.start() + "+") + " kişi";
    }

    @Override
    public void renderState(ConversationContext ctx, ConversationState state, ConversationContextData data) {
        ctx.conversation().setState(state);
        switch (state) {
            case GUEST_RANGE_SELECT -> sendGuestRangeList(ctx);
            case HALL_SELECT -> sendHallList(ctx, 0);
            case MENU_SELECT -> sendMenuList(ctx, 0);
            case MONTH_SELECT -> sendMonthList(ctx, 0);
            case WEEK_SELECT -> sendWeekList(ctx);
            case TIME_SLOT_SELECT -> sendTimeSlotPrompt(ctx);
            case CONFIRM -> sendConfirmSummary(ctx);
            default -> fallbackToIdle(ctx);
        }
    }

    private boolean isMenuAvailableForHall(Menu menu, java.util.UUID hallId) {
        if (hallId == null) return true;
        var links = hallMenuRepository.findByMenu_Id(menu.getId());
        if (links.isEmpty()) return true; // menuye hic salon atanmamissa tum salonlarda gecerli sayilir
        return links.stream().anyMatch(hm -> hm.getHall().getId().equals(hallId));
    }

    private String capacityLabel(Hall h) {
        if (h.getCapacityMin() != null && h.getCapacityMax() != null) {
            return h.getCapacityMin() + "-" + h.getCapacityMax() + " kişi";
        }
        if (h.getCapacityMax() != null) return "En fazla " + h.getCapacityMax() + " kişi";
        return null;
    }

    private String priceLabel(ConversationContext ctx, Menu m) {
        if (ctx.tenant().isShowPrices() && m.getPricePerPerson() != null) {
            return m.getPricePerPerson().stripTrailingZeros().toPlainString() + " TL/kişi";
        }
        return null;
    }

    private void fallbackToIdle(ConversationContext ctx) {
        whatsAppClient.sendText(ctx.accessToken(), ctx.phoneNumberId(), ctx.customer().toWaTarget(), MessageUtil.PROBLEM_OCCURED);
        ctx.conversation().setState(ConversationState.IDLE);
    }
}
