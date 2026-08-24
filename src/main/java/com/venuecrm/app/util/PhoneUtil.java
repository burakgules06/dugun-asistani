package com.venuecrm.app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtil {

    private static final Pattern DIGIT_RUN = Pattern.compile("\\d{8,}");

    private PhoneUtil() {}

    /** Loglama icin telefon numarasini maskeler: son 4 hane haric hepsini * yapar. */
    public static String mask(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        int visible = 4;
        return "*".repeat(phone.length() - visible) + phone.substring(phone.length() - visible);
    }

    /** Serbest metin icindeki 8+ hanelik rakam dizilerini maskeler. */
    public static String maskDigitRuns(String text) {
        if (text == null) return null;
        Matcher m = DIGIT_RUN.matcher(text);
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (m.find()) {
            sb.append(text, last, m.start());
            sb.append(mask(m.group()));
            last = m.end();
        }
        sb.append(text.substring(last));
        return sb.toString();
    }

    /**
     * Telefon numarasini standart formata cevirir: 90XXXXXXXXXX
     */
    public static String normalize(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isBlank()) return null;

        if (digits.startsWith("00")) digits = digits.substring(2);

        if (digits.length() == 10 && digits.startsWith("5")) {
            return "90" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("0")) {
            return "90" + digits.substring(1);
        }
        if (digits.length() == 12 && digits.startsWith("90")) {
            return digits;
        }
        return digits;
    }
}
