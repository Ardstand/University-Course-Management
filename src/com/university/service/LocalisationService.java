package com.university.service;

import com.university.model.Student;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * OOP2 Feature: Localisation — Locale, NumberFormat, DateTimeFormatter, ResourceBundle.
 */
public class LocalisationService {

    public static final Locale LOCALE_IE = Locale.forLanguageTag("en-IE");
    public static final Locale LOCALE_US = Locale.US;
    public static final Locale LOCALE_IN = Locale.forLanguageTag("en-IN");

    public String formatGpa(double gpa, Locale locale) {
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMinimumFractionDigits(2); nf.setMaximumFractionDigits(2);
        return nf.format(gpa);
    }

    public String formatGpaAsPercentage(double gpa, Locale locale) {
        NumberFormat pf = NumberFormat.getPercentInstance(locale);
        pf.setMinimumFractionDigits(1);
        return pf.format(gpa / 4.0);
    }

    public String formatDate(LocalDate date, Locale locale) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale));
    }

    public String formatDateLong(LocalDate date, Locale locale) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale));
    }

    public ResourceBundle loadBundle(Locale locale) {
        try { return ResourceBundle.getBundle("com.university.i18n.messages", locale); }
        catch (MissingResourceException e) {
            return ResourceBundle.getBundle("com.university.i18n.messages", Locale.ENGLISH);
        }
    }

    public String getMessage(String key, Locale locale) {
        try { return loadBundle(locale).getString(key); }
        catch (MissingResourceException e) { return key; }
    }

    public void printLocalisedRecord(Student student, Locale locale) {
        System.out.println("  Locale   : " + locale.getDisplayName());
        System.out.println("  " + getMessage("label.studentId", locale) + " : " + student.getStudentId());
        System.out.println("  " + getMessage("label.name", locale)      + " : " + student.getFullName());
        System.out.println("  " + getMessage("label.major", locale)     + " : " + student.getMajor().getFullName());
        System.out.println("  " + getMessage("label.gpa", locale)       + " : " + formatGpa(student.getGPA(), locale));
        System.out.println("  " + getMessage("label.gpaPercent", locale) + " : " + formatGpaAsPercentage(student.getGPA(), locale));
        System.out.println("  " + getMessage("label.enrolled", locale)  + " : " + formatDateLong(student.getEnrollmentDate(), locale));
        System.out.println("  " + getMessage("label.standing", locale)  + " : " + student.getAcademicStanding());
    }

    public void printMultiLocaleGpa(double gpa) {
        System.out.printf("  GPA value  : %.2f (raw)%n", gpa);
        for (Locale l : List.of(LOCALE_IE, LOCALE_US, LOCALE_IN))
            System.out.printf("  %-8s → %s  (%s)%n", l.getCountry(), formatGpa(gpa, l), formatGpaAsPercentage(gpa, l));
    }

    public void printMultiLocaleDate(LocalDate date) {
        System.out.println("  Date (raw) : " + date);
        for (Locale l : List.of(LOCALE_IE, LOCALE_US, LOCALE_IN))
            System.out.printf("  %-8s → %s%n", l.getCountry(), formatDate(date, l));
    }

    public Locale fromMenuChoice(int choice) {
        return switch (choice) { case 1 -> LOCALE_IE; case 2 -> LOCALE_US; case 3 -> LOCALE_IN; default -> LOCALE_IE; };
    }

    public Map<Integer, String> localeMenu() {
        return Map.of(1, "Ireland (en-IE)", 2, "United States (en-US)", 3, "India (en-IN)");
    }
}
