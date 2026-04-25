package com.university.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Student — a final class in the sealed Person hierarchy.
 *
 * OOP2 Feature (JEP 513): In Java 25, constructor bodies can contain
 * statements BEFORE super() for pre-validation. In Java 21 (this build),
 * super() must remain first; the same validation logic is placed immediately
 * after super() to demonstrate the intent. See comments marked [JEP 513].
 */
public final class Student extends Person implements Enrollable, Gradeable {

    private String studentId;
    private DepartmentType major;
    private List<Grade> grades;
    private LocalDate enrollmentDate;
    private double gpa;
    private boolean active;
    private static int studentCounter = 0;

    /**
     * OOP2 — JEP 513 Flexible Constructor Bodies (Java 25).
     *
     * Java 25 would allow:
     *   // PRE-super validation:
     *   if (firstName == null || firstName.isBlank()) throw ...
     *   String normFirst = firstName.trim()...
     *   super(normFirst, normLast, email.toLowerCase());
     *
     * Java 21 equivalent shown here (same logic, after super):
     */
    public Student(String firstName, String lastName, String email, DepartmentType major) {
        super(firstName != null ? firstName.trim() : firstName,
              lastName  != null ? lastName.trim()  : lastName,
              email     != null ? email.trim().toLowerCase() : null);

        // [JEP 513] In Java 25 these checks would run BEFORE super()
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("Student first name cannot be blank");
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Student last name cannot be blank");
        if (major == null)
            throw new IllegalArgumentException("Major cannot be null");

        // Normalise capitalisation (in Java 25 this would happen pre-super)
        String normFirst = getFirstName().substring(0, 1).toUpperCase()
                + getFirstName().substring(1).toLowerCase();
        String normLast  = getLastName().substring(0, 1).toUpperCase()
                + getLastName().substring(1).toLowerCase();
        setFirstName(normFirst);
        setLastName(normLast);

        this.studentId      = generateStudentId();
        this.major          = major;
        this.grades         = new ArrayList<>();
        this.enrollmentDate = LocalDate.now();
        this.gpa            = 0.0;
        this.active         = true;
    }

    public Student(String firstName, String lastName, String email,
                   String phone, LocalDate dateOfBirth, DepartmentType major) {
        super(firstName, lastName, email, phone, dateOfBirth);

        // [JEP 513] In Java 25 these checks would run BEFORE super()
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        if (major == null)
            throw new IllegalArgumentException("Major cannot be null");

        this.studentId      = generateStudentId();
        this.major          = major;
        this.grades         = new ArrayList<>();
        this.enrollmentDate = LocalDate.now();
        this.gpa            = 0.0;
        this.active         = true;
    }

    @Override public String getRole() { return "Student"; }

    public String getDetailedInfo() {
        return super.toString() + ", Student ID: " + studentId
                + ", Major: " + major + ", GPA: " + String.format("%.2f", gpa);
    }

    // varargs — OOP1 feature
    public void addGrade(Grade grade) {
        if (grade == null) throw new IllegalArgumentException("Grade cannot be null");
        grades.add(grade);
        calculateGPA();
    }

    public void addGrade(Grade... newGrades) {
        for (Grade g : newGrades) if (g != null) grades.add(g);
        calculateGPA();
    }

    public void addGrade(List<Grade> newGrades) {
        if (newGrades != null) { grades.addAll(newGrades); calculateGPA(); }
    }

    private void calculateGPA() {
        if (grades.isEmpty()) { this.gpa = 0.0; return; }
        double total = grades.stream().mapToDouble(Grade::getGradePoint).sum();
        this.gpa = Math.round((total / grades.size()) * 100.0) / 100.0;
    }

    private static String generateStudentId() {
        return "STU" + String.format("%05d", ++studentCounter);
    }

    @Override public String getEnrollmentId()     { return studentId; }
    @Override public LocalDate getEnrollmentDate() { return enrollmentDate; }
    @Override public boolean isActive()            { return active; }
    @Override public double getGPA()               { return gpa; }
    @Override public List<Grade> getGrades()       { return new ArrayList<>(grades); }

    public String getStudentId()            { return studentId; }
    public DepartmentType getMajor()        { return major; }
    public void setMajor(DepartmentType m)  { this.major = m; }
    public void setActive(boolean active)   { this.active = active; }
    public void addLoyaltyPoints(double p)  {}

    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', major=%s, gpa=%.2f}",
                studentId, getFullName(), major, gpa);
    }
}
