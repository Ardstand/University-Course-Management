package com.university.service;

import com.university.model.DepartmentType;
import com.university.model.Student;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * OOP2 Features: Streams (all terminal + intermediate ops), Collectors,
 * Comparator.comparing(), Consumer, Function, Supplier lambdas.
 */
public class ReportService {

    // Sorting — Comparator.comparing()
    public List<Student> sortByGpaDescending(List<Student> students) {
        return students.stream()
                .sorted(Comparator.comparingDouble(Student::getGPA).reversed()
                        .thenComparing(Comparator.comparing(Student::getFullName)))
                .collect(Collectors.toList());
    }

    public List<Student> sortByName(List<Student> students) {
        return students.stream()
                .sorted(Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName))
                .collect(Collectors.toList());
    }

    public List<Student> sortByEnrollmentDate(List<Student> students) {
        return students.stream()
                .sorted(Comparator.comparing(Student::getEnrollmentDate))
                .collect(Collectors.toList());
    }

    // Terminal ops
    public Optional<Student> findTopStudent(List<Student> students) {
        return students.stream().max(Comparator.comparingDouble(Student::getGPA));
    }

    public Optional<Student> findLowestGpaStudent(List<Student> students) {
        return students.stream().min(Comparator.comparingDouble(Student::getGPA));
    }

    public long countAboveGpa(List<Student> students, double threshold) {
        return students.stream().filter(s -> s.getGPA() >= threshold).count();
    }

    public Optional<Student> findFirstByName(List<Student> students, String query) {
        return students.stream()
                .filter(s -> s.getFullName().toLowerCase().contains(query.toLowerCase()))
                .findFirst();
    }

    public Optional<Student> findAnyActiveStudent(List<Student> students) {
        return students.stream().filter(Student::isActive).findAny();
    }

    public boolean areAllPassing(List<Student> students) {
        return students.stream().allMatch(s -> s.getGPA() >= 2.0);
    }

    public boolean hasHonourRollStudent(List<Student> students) {
        return students.stream().anyMatch(s -> s.getGPA() >= 3.5);
    }

    public boolean noneOnProbation(List<Student> students) {
        return students.stream().noneMatch(s -> s.getGPA() < 2.0 && !s.getGrades().isEmpty());
    }

    public void printStudentSummaries(List<Student> students) {
        Consumer<Student> printSummary = s ->
                System.out.printf("  %-20s | %-20s | GPA: %.2f%n",
                        s.getStudentId(), s.getFullName(), s.getGPA());
        System.out.println("  Student ID           | Name                 | GPA");
        System.out.println("  " + "-".repeat(52));
        students.stream().sorted(Comparator.comparing(Student::getFullName)).forEach(printSummary);
    }

    // Intermediate ops
    public List<DepartmentType> getRepresentedDepartments(List<Student> students) {
        return students.stream()
                .map(Student::getMajor).distinct().sorted().collect(Collectors.toList());
    }

    public List<String> getTopStudentNames(List<Student> students, double minGpa, int limit) {
        return students.stream()
                .filter(s -> s.getGPA() >= minGpa)
                .sorted(Comparator.comparingDouble(Student::getGPA).reversed())
                .limit(limit)
                .map(Student::getFullName)
                .collect(Collectors.toList());
    }

    // Collectors
    public Map<DepartmentType, List<Student>> groupByDepartment(List<Student> students) {
        return students.stream().collect(Collectors.groupingBy(Student::getMajor));
    }

    public Map<Boolean, List<Student>> partitionByPassFail(List<Student> students) {
        return students.stream()
                .filter(s -> !s.getGrades().isEmpty())
                .collect(Collectors.partitioningBy(s -> s.getGPA() >= 2.0));
    }

    public Map<String, Double> buildGpaLookup(List<Student> students) {
        return students.stream()
                .collect(Collectors.toMap(Student::getStudentId, Student::getGPA));
    }

    public Map<DepartmentType, Long> countByDepartment(List<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
    }

    public Map<DepartmentType, Double> averageGpaByDepartment(List<Student> students) {
        return students.stream()
                .filter(s -> !s.getGrades().isEmpty())
                .collect(Collectors.groupingBy(Student::getMajor,
                        Collectors.averagingDouble(Student::getGPA)));
    }

    // Function and Supplier lambdas
    public List<String> formatStudents(List<Student> students, Function<Student, String> formatter) {
        return students.stream().map(formatter).collect(Collectors.toList());
    }

    public Supplier<Integer> studentCountSupplier(List<Student> students) {
        return students::size;
    }

    public void printStats(List<Student> students) {
        System.out.println("  Total students  : " + students.size());
        findTopStudent(students).ifPresentOrElse(
                s -> System.out.printf("  Highest GPA     : %s (%.2f)%n", s.getFullName(), s.getGPA()),
                () -> System.out.println("  Highest GPA     : N/A"));
        findLowestGpaStudent(students).ifPresentOrElse(
                s -> System.out.printf("  Lowest GPA      : %s (%.2f)%n", s.getFullName(), s.getGPA()),
                () -> System.out.println("  Lowest GPA      : N/A"));
        System.out.printf("  All passing     : %s%n", areAllPassing(students));
        System.out.printf("  Has honour roll : %s%n", hasHonourRollStudent(students));
    }
}
