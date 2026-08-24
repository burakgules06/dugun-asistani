package com.venuecrm.app.util;

public class MessageUtil {

    private MessageUtil() {}

    public static final String SUBSCRIPTION_INACTIVE =
            "Şu anda sistemimiz hizmet veremiyor. Lütfen bizi telefonla arayınız.";

    public static final String PROBLEM_OCCURED = "Bir sorun oluştu, baştan deneyelim.";
    public static final String BACK_HINT = "\n\n_Bir önceki adıma dönmek için \"geri\" yazabilirsiniz._";
    public static final String NO_BACK_STEP = "Şu an en baştasınız, geri gidilecek bir adım yok.";
    public static final String INVALID_SELECTION = "Geçersiz seçim. Baştan deneyelim.";

    // ---- İsim ----
    public static final String ASK_NAME_PROMPT =
            "Merhaba! 👋\n\nSize hitap edebilmemiz için isminizi öğrenebilir miyiz?";
    public static final String ASK_NAME_INVALID = "Lütfen geçerli bir isim yazar mısınız?";
    public static final String NAME_SAVED_TEMPLATE = "Teşekkürler %s! 🙂";

    // ---- Kişi sayısı aralığı seçimi ----
    public static final String SELECT_GUEST_RANGE_PROMPT_TEMPLATE =
            "*%s Akıllı Planlama Sistemine Hoşgeldiniz*\n\nDavetiniz için planladığınız kişi sayısı aralığını seçiniz.";
    public static final String LIST_BTN_GUEST_RANGE = "Kişi Sayısı";
    public static final String PLEASE_SELECT_GUEST_RANGE = "Lütfen listeden bir kişi sayısı aralığı seçin.";

    // ---- Salon seçimi ----
    public static final String SELECT_HALL_PROMPT = "Hangi salonumuzu tercih edersiniz?" + BACK_HINT;
    public static final String LIST_BTN_HALL = "Salon Seç";
    public static final String NO_HALL_DEFINED = "Şu an tanımlı bir salonumuz bulunmuyor.";
    public static final String HALL_NOT_FOUND = "Salon bulunamadı. Baştan deneyelim.";
    public static final String PLEASE_SELECT_HALL = "Lütfen listeden bir salon seçin.";

    // ---- Menü seçimi ----
    public static final String SELECT_MENU_PROMPT = "Hangi menü paketini tercih edersiniz?" + BACK_HINT;
    public static final String LIST_BTN_MENU = "Menü Seç";
    public static final String NO_MENU_DEFINED = "Şu an tanımlı bir menümüz bulunmuyor.";
    public static final String NO_MENU_AVAILABLE_FOR_PERIOD = "Seçtiğiniz salon ve ay için şu an sunabileceğimiz bir menü bulunmuyor. Farklı bir ay deneyebilir ya da bizi arayabilirsiniz.";
    public static final String MENU_NOT_FOUND = "Menü bulunamadı. Baştan deneyelim.";
    public static final String PLEASE_SELECT_MENU = "Lütfen listeden bir menü seçin.";

    // ---- Ay / hafta / gün tipi tercihi (kesin tarih değil - kesin tarihi işletme belirler) ----
    public static final String SELECT_MONTH_PROMPT = "Hangi ay için düğün planlıyorsunuz?" + BACK_HINT;
    public static final String LIST_BTN_MONTH = "Ay Seç";
    public static final String PLEASE_SELECT_MONTH = "Lütfen listeden bir ay seçin.";
    public static final String INVALID_MONTH = "Geçersiz ay seçimi. Baştan deneyelim.";

    public static final String SELECT_WEEK_PROMPT = "Ayın hangi haftasını tercih edersiniz?" + BACK_HINT;
    public static final String LIST_BTN_WEEK = "Hafta Seç";
    public static final String PLEASE_SELECT_WEEK = "Lütfen listeden bir hafta seçin.";
    public static final String INVALID_WEEK = "Geçersiz hafta seçimi. Baştan deneyelim.";
    public static final String WEEK_LABEL_TEMPLATE = "%d. Hafta"; // {0}=hafta no

    // ---- Zaman dilimi seçimi (hafta içi akşam / hafta sonu akşam / hafta sonu gündüz) ----
    public static final String SELECT_TIME_SLOT_PROMPT = "Hangi zaman dilimini tercih edersiniz?" + BACK_HINT;
    public static final String BTN_TIME_SLOT_WEEKDAY_EVENING = "📅 Haftaiçi Akşam";
    public static final String BTN_TIME_SLOT_WEEKEND_EVENING = "🎉 Haftasonu Akşam";
    public static final String BTN_TIME_SLOT_WEEKEND_DAY = "☀️ Haftasonu Gündüz";
    public static final String PLEASE_SELECT_TIME_SLOT = "Lütfen listeden bir zaman dilimi seçin.";
    public static final String NO_TIME_SLOT_AVAILABLE = "Seçtiğiniz salon, ay ve menü için şu an uygun bir zaman dilimi bulunmuyor. Farklı bir ay ya da menü deneyebilir veya bizi arayabilirsiniz.";

    // ---- Onay ----
    // NOT: Bu talep henuz kesinlesmis bir tarih icermez - musterinin tercih ettigi
    // donem (ay/hafta/gun tipi) burada gosterilir; kesin tarihi isletme panelden belirler.
    public static final String CONFIRM_SUMMARY_TEMPLATE =
            "*Teklif Talebi Özeti*\n\n"
                    + "🏛️ %s\n"
                    + "🍽️ %s\n"
                    + "📅 %s\n"
                    + "👥 %s kişi\n\n"
                    + "Kesin tarih, ekibimizle görüşmeniz sonrası netleşecektir.\n\n"
                    + "Bu bilgilerle talebinizi ekibimize iletelim mi?";
    public static final String BTN_CONFIRM_YES = "✅ Evet, Gönder";
    public static final String BTN_CONFIRM_NO = "❌ Vazgeç";
    public static final String PLEASE_CONFIRM_OR_CANCEL = "Lütfen Evet ya da Vazgeç seçin.";
    public static final String REQUEST_CANCELLED = "Talebiniz iptal edildi. Başka bir şey ister misiniz?";

    public static final String REQUEST_RECEIVED_TEMPLATE =
            "Teşekkürler! 🎉\n\nTalebiniz ekibimize ulaştı. En kısa sürede sizinle iletişime geçeceğiz.";

    // Profil adini temizle: emoji/ozel karakter at, harf+bosluk birak
    public static String cleanName(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String cleaned = raw.replaceAll("[^\\p{L}\\s]", "").trim();
        cleaned = cleaned.replaceAll("\\s+", " ");
        return cleaned.length() >= 2 ? cleaned : null;
    }
}
