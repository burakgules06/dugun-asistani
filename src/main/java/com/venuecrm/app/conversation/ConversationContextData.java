package com.venuecrm.app.conversation;

import com.venuecrm.app.model.enums.ConversationState;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * wa_conversation.context (JSONB) icinde biriken veri.
 * Teklif akisi ilerledikce dolar: once kisi sayisi araligi (ve bu araliga gore hallId),
 * sonra tercih edilen ay/hafta, sonra menuId, sonra zaman dilimi. Musteri kesin bir tarih
 * vermez - kesin tarihi personel panelden belirler (bkz. Lead.eventDate).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationContextData {
    private static final int MAX_HISTORY = 10;

    private Integer guestCountMin;
    private Integer guestCountMax; // null = ust siniri olmayan (acik uclu) aralik
    private UUID hallId;
    private UUID menuId;
    private String preferredMonth;  // ISO, ayin ilk gunu: "2027-08-01"
    private Integer preferredWeek;  // 1-5 (ayin kacinci haftasi)
    private String preferredTimeSlot; // TimeSlot adi: "WEEKDAY_EVENING" | "WEEKEND_EVENING" | "WEEKEND_DAY"
    private List<String> history = new ArrayList<>(); // "geri" komutu icin ziyaret edilen state'ler (LIFO)

    public Integer getGuestCountMin() { return guestCountMin; }
    public void setGuestCountMin(Integer guestCountMin) { this.guestCountMin = guestCountMin; }
    public Integer getGuestCountMax() { return guestCountMax; }
    public void setGuestCountMax(Integer guestCountMax) { this.guestCountMax = guestCountMax; }
    public UUID getHallId() { return hallId; }
    public void setHallId(UUID hallId) { this.hallId = hallId; }
    public UUID getMenuId() { return menuId; }
    public void setMenuId(UUID menuId) { this.menuId = menuId; }
    public String getPreferredMonth() { return preferredMonth; }
    public void setPreferredMonth(String preferredMonth) { this.preferredMonth = preferredMonth; }
    public Integer getPreferredWeek() { return preferredWeek; }
    public void setPreferredWeek(Integer preferredWeek) { this.preferredWeek = preferredWeek; }
    public String getPreferredTimeSlot() { return preferredTimeSlot; }
    public void setPreferredTimeSlot(String preferredTimeSlot) { this.preferredTimeSlot = preferredTimeSlot; }
    public List<String> getHistory() { return history; }
    public void setHistory(List<String> history) { this.history = history != null ? history : new ArrayList<>(); }

    public void pushHistory(ConversationState state) {
        if (state == null) return;
        if (history.size() >= MAX_HISTORY) history.remove(0);
        history.add(state.name());
    }

    public ConversationState popHistory() {
        if (history.isEmpty()) return null;
        String last = history.remove(history.size() - 1);
        try {
            return ConversationState.valueOf(last);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }
}
