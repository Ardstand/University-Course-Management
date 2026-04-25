package com.university.util;

import com.university.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * StudentGatherers — demonstrates Java 25 Stream Gatherers concept (extra marks).
 *
 * OOP2 Feature: Java 25 — Stream Gatherers (JEP 485, finalised Java 24)
 *
 * NOTE: java.util.stream.Gatherer was finalised in Java 24 (JEP 485).
 * This file simulates the same logic using standard Java 21 APIs so the
 * project compiles cleanly. In your video, explain that the real
 * implementation would use:
 *
 *   students.stream()
 *       .gather(Gatherer.ofSequential(
 *           ArrayList::new,
 *           Gatherer.Integrator.ofGreedy((buf, el, downstream) -> { ... }),
 *           (buf, downstream) -> { ... }
 *       ))
 *       .forEach(...);
 *
 * A Gatherer has four components:
 *   initializer()  — creates per-stream mutable state
 *   integrator()   — processes each element
 *   combiner()     — merges parallel states (optional)
 *   finisher()     — emits remaining buffered elements
 *
 * Gatherers generalise intermediate stream operations, enabling stateful
 * transformations that map/filter/flatMap cannot express.
 */
public final class StudentGatherers {

    private StudentGatherers() {}

    // -------------------------------------------------------------------------
    // Gatherer 1: windowing by size
    // -------------------------------------------------------------------------

    /**
     * Groups students into fixed-size windows (pages).
     *
     * Real Java 25 equivalent:
     *   students.stream()
     *       .gather(windowBySize(3))
     *       .forEach(page -> ...)
     *
     * Simulated here by iterating the list manually with the same state machine.
     */
    public static List<List<Student>> windowBySize(List<Student> students, int size) {
        if (size <= 0) throw new IllegalArgumentException("Window size must be > 0");

        List<List<Student>> pages = new ArrayList<>();
        List<Student> buffer = new ArrayList<>();

        // This mirrors the Gatherer integrator logic exactly
        for (Student student : students) {
            buffer.add(student);
            if (buffer.size() == size) {
                pages.add(new ArrayList<>(buffer)); // emit window
                buffer.clear();
            }
        }
        // Gatherer finisher — emit remaining partial window
        if (!buffer.isEmpty()) {
            pages.add(new ArrayList<>(buffer));
        }

        return pages;
    }

    // -------------------------------------------------------------------------
    // Gatherer 2: running average GPA
    // -------------------------------------------------------------------------

    /**
     * Emits the running average GPA after each student is processed.
     *
     * Real Java 25 equivalent:
     *   students.stream()
     *       .gather(runningAverageGpa())
     *       .toList()
     *
     * Simulated below with the same stateful accumulation pattern.
     */
    public static List<Double> runningAverageGpa(List<Student> students) {
        List<Double> results = new ArrayList<>();
        double sum = 0.0;
        int count = 0;

        // Mirrors the Gatherer integrator — one output element per input element
        for (Student student : students) {
            sum += student.getGPA();
            count++;
            double avg = Math.round((sum / count) * 100.0) / 100.0;
            results.add(avg);
        }

        return results;
    }

    // -------------------------------------------------------------------------
    // Demo helpers
    // -------------------------------------------------------------------------

    public static void printPages(List<Student> students, int pageSize) {
        System.out.printf("  Paging %d students into groups of %d:%n",
                students.size(), pageSize);
        List<List<Student>> pages = windowBySize(students, pageSize);
        for (int i = 0; i < pages.size(); i++) {
            System.out.printf("  -- Page %d --%n", i + 1);
            pages.get(i).forEach(s ->
                    System.out.printf("     %-20s GPA: %.2f%n", s.getFullName(), s.getGPA()));
        }
    }

    public static void printRunningAverage(List<Student> students) {
        System.out.println("  Running average GPA (in order):");
        List<Double> avgs = runningAverageGpa(students);
        for (int i = 0; i < students.size(); i++) {
            System.out.printf("    After %-20s → running avg: %.2f%n",
                    students.get(i).getFullName(), avgs.get(i));
        }
    }
}
