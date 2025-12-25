package utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DateUtils {
    private DateUtils() {
    }

    public static LocalDate getNextMonthDate(int dayOfMonth) {
        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        int safeDay = Math.min(dayOfMonth, nextMonth.lengthOfMonth());
        return nextMonth.withDayOfMonth(safeDay);
    }

    public static LocalDate getNextOccurrence(String monthName, int dayOfMonth) {
        if (monthName == null || monthName.trim().isEmpty()) {
            return getNextMonthDate(dayOfMonth);
        }
        Month month = parseMonth(monthName);
        LocalDate now = LocalDate.now();
        int safeDay = Math.min(dayOfMonth, month.length(Year.isLeap(now.getYear())));
        LocalDate candidate = LocalDate.of(now.getYear(), month, safeDay);
        if (!candidate.isAfter(now)) {
            int nextYear = now.getYear() + 1;
            safeDay = Math.min(dayOfMonth, month.length(Year.isLeap(nextYear)));
            candidate = LocalDate.of(nextYear, month, safeDay);
        }
        return candidate;
    }

    public static String formatForAriaLabel(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    public static List<String> getAriaLabelCandidates(LocalDate date) {
        List<String> labels = new ArrayList<>();
        labels.add(formatForAriaLabel(date));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE, d MMMM yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE d MMMM yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE, MMMM d, yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE MMMM d yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE MMM d", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE, d MMMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("EEE d MMMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)));
        labels.add(date.format(DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH)));
        return labels;
    }

    public static String toIsoDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String formatAsDayMonthYear(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static String formatAsCompactDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private static Month parseMonth(String monthName) {
        String normalized = monthName.trim().toUpperCase(Locale.ENGLISH);
        if (normalized.matches("\\d{1,2}")) {
            return Month.of(Integer.parseInt(normalized));
        }
        for (Month month : Month.values()) {
            String full = month.name();
            String shortName = full.substring(0, 3);
            if (full.equals(normalized) || shortName.equals(normalized)) {
                return month;
            }
        }
        throw new IllegalArgumentException("Unsupported month: " + monthName);
    }
}
