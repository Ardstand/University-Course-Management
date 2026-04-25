package com.university.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Instructor — a final class in the sealed Person hierarchy.
 *
 * OOP2 Feature (JEP 513): In Java 25, validation runs BEFORE super().
 * In Java 21 (this build), super() is first and validation follows.
 * See [JEP 513] comments for what the Java 25 form would look like.
 */
public final class Instructor extends Person {

    private String instructorId;
    private DepartmentType department;
    private String[] officeHours;
    private List<String> coursesTaught;
    private double salary;
    private static int instructorCounter = 0;

    /**
     * OOP2 — JEP 513 Flexible Constructor Bodies (Java 25).
     *
     * Java 25 would allow:
     *   // PRE-super validation:
     *   if (department == null) throw ...
     *   if (salary < 0) throw ...
     *   String normEmail = email.trim().toLowerCase();
     *   super(firstName, lastName, normEmail);
     *
     * Java 21 equivalent shown here:
     */
    public Instructor(String firstName, String lastName, String email,
                     DepartmentType department, double salary) {
        super(firstName, lastName, email != null ? email.trim().toLowerCase() : null);

        // [JEP 513] In Java 25 these checks would run BEFORE super()
        if (department == null)
            throw new IllegalArgumentException("Instructor department cannot be null");
        if (salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative");

        this.instructorId  = generateInstructorId();
        this.department    = department;
        this.salary        = salary;
        this.officeHours   = new String[0];
        this.coursesTaught = new ArrayList<>();
    }

    public Instructor(String firstName, String lastName, String email,
                     String phone, LocalDate dateOfBirth,
                     DepartmentType department, double salary) {
        super(firstName, lastName,
              email != null ? email.trim().toLowerCase() : null,
              phone, dateOfBirth);

        // [JEP 513] In Java 25 these checks would run BEFORE super()
        if (department == null)
            throw new IllegalArgumentException("Instructor department cannot be null");
        if (salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative");

        this.instructorId  = generateInstructorId();
        this.department    = department;
        this.salary        = salary;
        this.officeHours   = new String[0];
        this.coursesTaught = new ArrayList<>();
    }

    @Override
    public String getRole() { return "Instructor - " + department.getFullName(); }

    public String getInstructorProfile() {
        return super.getFullName() + " - " + department.getCode() + " Department";
    }

    public void setOfficeHours(String... hours) { this.officeHours = hours; }
    public String[] getOfficeHours()             { return officeHours.clone(); }

    public void addCourse(String courseCode) {
        if (!coursesTaught.contains(courseCode)) coursesTaught.add(courseCode);
    }

    private static String generateInstructorId() {
        return "INS" + String.format("%05d", ++instructorCounter);
    }

    public String getInstructorId()             { return instructorId; }
    public DepartmentType getDepartment()       { return department; }
    public void setDepartment(DepartmentType d) { this.department = d; }
    public double getSalary()                   { return salary; }
    public void setSalary(double s)             { this.salary = s; }
    public List<String> getCoursesTaught()      { return new ArrayList<>(coursesTaught); }

    @Override
    public String toString() {
        return String.format("Instructor{id='%s', name='%s', department=%s}",
                instructorId, getFullName(), department);
    }
}
