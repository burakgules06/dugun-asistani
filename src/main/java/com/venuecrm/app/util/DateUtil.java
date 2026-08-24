package com.venuecrm.app.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    private DateUtil() {}

    private static final Locale TR = new Locale("tr", "TR");
    public static final DateTimeFormatter DATE_FULL_TR = DateTimeFormatter.ofPattern("d MMMM EEEE", TR);
    public static final DateTimeFormatter DATE_SHORT_TR = DateTimeFormatter.ofPattern("d MMMM yyyy", TR);
    private static final DateTimeFormatter MONTH_YEAR_TR = DateTimeFormatter.ofPattern("MMMM yyyy", TR);
    private static final DateTimeFormatter DAY_MONTH_TR = DateTimeFormatter.ofPattern("d MMMM", TR);

    /** Cuma/Cumartesi haric her gunu haftaici sayar (TR pazar tatili yerine iş kuralı: Cmt+Paz haftasonu). */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek d = date.getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }

    public static String formatDisplay(LocalDate date) {
        return date.format(DATE_FULL_TR);
    }

    /** "Ağustos 2027" */
    public static String monthLabel(YearMonth ym) {
        return capitalize(ym.atDay(1).format(MONTH_YEAR_TR));
    }

    /** Bir ayin kac haftaya (1-5) bolundugunu doner: her hafta 7 gunluk sabit blok (1-7, 8-14, ...). */
    public static int weekCountInMonth(YearMonth ym) {
        return (int) Math.ceil(ym.lengthOfMonth() / 7.0);
    }

    /** "1-7 Ağustos" bicimindeki hafta araligi etiketi. week: 1-5 arasi. */
    public static String weekRangeLabel(YearMonth ym, int week) {
        int start = (week - 1) * 7 + 1;
        int end = Math.min(week * 7, ym.lengthOfMonth());
        LocalDate startDate = ym.atDay(start);
        LocalDate endDate = ym.atDay(end);
        return startDate.getDayOfMonth() + "-" + endDate.format(DAY_MONTH_TR);
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
