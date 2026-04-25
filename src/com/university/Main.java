package com.university;

import com.university.exception.CourseFullException;
import com.university.exception.EnrollmentException;
import com.university.model.Course;
import com.university.model.CourseSchedule;
import com.university.model.DepartmentType;
import com.university.model.Enrollment;
import com.university.model.Grade;
import com.university.model.Instructor;
import com.university.model.Person;
import com.university.model.Student;
import com.university.model.Transcript;
import com.university.service.BatchProcessingService;
import com.university.service.EnrollmentService;
import com.university.service.FileService;
import com.university.service.LocalisationService;
import com.university.service.ReportService;
import com.university.service.StudentService;
import com.university.util.GradeUtil;
import com.university.util.ScopedContext;
import com.university.util.StudentGatherers;
import com.university.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * University Course Management System — OOP2 Edition
 * Demonstrates all OOP2 features via an interactive menu.
 *
 * OOP2 Menu:
 *   10. Streams & Sorting        [US1-US4]
 *   11. Department Report        [US3 — Collectors]
 *   12. Batch Grade Processing   [US5 — ExecutorService + Callable]
 *   13. NIO2 Export Transcripts  [US6]
 *   14. NIO2 Import Students     [US6]
 *   15. Localisation             [US7]
 *   16. Java 25 Extras           [US10-US11 — ScopedValue + Gatherers]
 *   17. Java 25 JEP 513 Demo     [US9]
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    // OOP1 services
    private static final StudentService    studentService    = new StudentService();
    private static final EnrollmentService enrollmentService = new EnrollmentService();

    // OOP2 services
    private static final ReportService          reportService = new ReportService();
    private static final BatchProcessingService batchService  = new BatchProcessingService();
    private static final FileService            fileService   = new FileService();
    private static final LocalisationService    localeService = new LocalisationService();

    private static final List<Course>     courses     = new ArrayList<>();
    private static final List<Instructor> instructors = new ArrayList<>();

    // =========================================================================
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("    UNIVERSITY COURSE MANAGEMENT SYSTEM");
        System.out.println("    OOP2 Assignment — Java Edition");
        System.out.println("=".repeat(70));
        System.out.println();
        initializeSampleData();
        boolean running = true;
        while (running) running = showMainMenu();
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Goodbye!");
        scanner.close();
    }

    // =========================================================================
    // MENU
    // =========================================================================

    private static boolean showMainMenu() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(70));
        System.out.println("--- OOP1 Features ---");
        System.out.println("1.  Add Student");
        System.out.println("2.  View All Students");
        System.out.println("3.  Search Student");
        System.out.println("4.  Enroll Student in Course");
        System.out.println("5.  Assign Grade");
        System.out.println("6.  View Student Transcript");
        System.out.println("7.  View All Courses");
        System.out.println("8.  View Reports");
        System.out.println("9.  OOP1 Features Demo");
        System.out.println("--- OOP2 New Features ---");
        System.out.println("10. Streams & Sorting Demo         [US1-US4]");
        System.out.println("11. Department Report (Collectors) [US3]");
        System.out.println("12. Batch Grade Processing         [US5 - ExecutorService]");
        System.out.println("13. Export Transcripts (NIO2)      [US6]");
        System.out.println("14. Import Students from CSV (NIO2)[US6]");
        System.out.println("15. Localisation Demo              [US7]");
        System.out.println("16. Java 25 Extras (Scoped+Gather) [US10-US11]");
        System.out.println("17. Java 25 JEP 513 Demo           [US9]");
        System.out.println("0.  Exit");
        System.out.println("=".repeat(70));
        System.out.print("Enter your choice: ");

        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return true;
            int choice = Integer.parseInt(input);
            System.out.println();
            switch (choice) {
                case 1  -> addStudent();
                case 2  -> viewAllStudents();
                case 3  -> searchStudent();
                case 4  -> enrollStudent();
                case 5  -> assignGrade();
                case 6  -> viewTranscript();
                case 7  -> viewAllCourses();
                case 8  -> viewReports();
                case 9  -> demonstrateOOP1();
                case 10 -> demonstrateStreamsAndSorting();
                case 11 -> demonstrateDepartmentReport();
                case 12 -> demonstrateBatchProcessing();
                case 13 -> demonstrateNio2Export();
                case 14 -> demonstrateNio2Import();
                case 15 -> demonstrateLocalisation();
                case 16 -> demonstrateJava25Extras();
                case 17 -> demonstrateJep513();
                case 0  -> { return false; }
                default -> System.out.println("Invalid choice! Please enter 0-17.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
        }
        return true;
    }

    // =========================================================================
    // OOP2 DEMOS
    // =========================================================================

    // Option 10 — Streams & Sorting
    private static void demonstrateStreamsAndSorting() {
        System.out.println("=".repeat(70));
        System.out.println("STREAMS & SORTING DEMO (US1-US4)");
        System.out.println("=".repeat(70));

        List<Student> all = studentService.getAllStudents();
        if (all.isEmpty()) { System.out.println("No students. Add some first or import CSV (14)."); return; }

        System.out.println("\n[1] Sorted by GPA descending (Comparator.comparing().reversed()):");
        reportService.sortByGpaDescending(all)
                .forEach(s -> System.out.printf("  %-20s GPA: %.2f%n", s.getFullName(), s.getGPA()));

        System.out.println("\n[2] Sorted by name (Comparator.comparing last then first):");
        reportService.sortByName(all)
                .forEach(s -> System.out.printf("  %s%n", s.getFullName()));

        System.out.println("\n[3] Sorted by enrollment date:");
        reportService.sortByEnrollmentDate(all)
                .forEach(s -> System.out.printf("  %-20s enrolled: %s%n", s.getFullName(), s.getEnrollmentDate()));

        System.out.println("\n[4] Terminal ops - min/max/count/findFirst/allMatch/anyMatch/noneMatch:");
        reportService.findTopStudent(all).ifPresent(s ->
                System.out.printf("  max()       - Top student  : %s (%.2f)%n", s.getFullName(), s.getGPA()));
        reportService.findLowestGpaStudent(all).ifPresent(s ->
                System.out.printf("  min()       - Lowest GPA   : %s (%.2f)%n", s.getFullName(), s.getGPA()));
        System.out.println("  count()     - GPA >= 3.0   : " + reportService.countAboveGpa(all, 3.0));
        reportService.findFirstByName(all, "a").ifPresentOrElse(
                s -> System.out.println("  findFirst() - Name has 'a'  : " + s.getFullName()),
                ()  -> System.out.println("  findFirst() - None found"));
        reportService.findAnyActiveStudent(all).ifPresentOrElse(
                s -> System.out.println("  findAny()   - Active student: " + s.getFullName()),
                ()  -> System.out.println("  findAny()   - None found"));
        System.out.println("  allMatch()  - All passing   : " + reportService.areAllPassing(all));
        System.out.println("  anyMatch()  - Any honour roll: " + reportService.hasHonourRollStudent(all));
        System.out.println("  noneMatch() - None probation : " + reportService.noneOnProbation(all));

        System.out.println("\n[5] Intermediate ops - filter/map/sorted/distinct/limit:");
        System.out.println("  Top 3 students above GPA 2.5 (filter+sorted+limit+map):");
        reportService.getTopStudentNames(all, 2.5, 3)
                .forEach(name -> System.out.println("    " + name));
        System.out.println("  Distinct departments (map+distinct+sorted):");
        reportService.getRepresentedDepartments(all)
                .forEach(d -> System.out.println("    " + d.getFullName()));

        System.out.println("\n[6] forEach with Consumer<Student>:");
        reportService.printStudentSummaries(all);
        pause();
    }

    // Option 11 — Collectors
    private static void demonstrateDepartmentReport() {
        System.out.println("=".repeat(70));
        System.out.println("DEPARTMENT REPORT - COLLECTORS DEMO (US3)");
        System.out.println("=".repeat(70));
        List<Student> all = studentService.getAllStudents();
        if (all.isEmpty()) { System.out.println("No students yet."); return; }

        System.out.println("\n[1] groupingBy(department):");
        reportService.groupByDepartment(all).forEach((dept, students) -> {
            System.out.println("  " + dept.getFullName() + " (" + students.size() + "):");
            students.forEach(s -> System.out.printf("    %-20s GPA: %.2f%n", s.getFullName(), s.getGPA()));
        });

        System.out.println("\n[2] partitioningBy(GPA >= 2.0) [graded students only]:");
        Map<Boolean, List<Student>> partition = reportService.partitionByPassFail(all);
        System.out.println("  PASSING (" + partition.get(true).size() + "):");
        partition.get(true).forEach(s ->
                System.out.printf("    %-20s GPA: %.2f%n", s.getFullName(), s.getGPA()));
        System.out.println("  FAILING (" + partition.get(false).size() + "):");
        partition.get(false).forEach(s ->
                System.out.printf("    %-20s GPA: %.2f%n", s.getFullName(), s.getGPA()));

        System.out.println("\n[3] toMap(studentId -> GPA):");
        reportService.buildGpaLookup(all).forEach((id, gpa) ->
                System.out.printf("  %s -> %.2f%n", id, gpa));

        System.out.println("\n[4] groupingBy + counting():");
        reportService.countByDepartment(all).forEach((dept, count) ->
                System.out.printf("  %-30s : %d%n", dept.getFullName(), count));

        System.out.println("\n[5] groupingBy + averagingDouble(GPA):");
        reportService.averageGpaByDepartment(all).forEach((dept, avg) ->
                System.out.printf("  %-30s : %.2f%n", dept.getFullName(), avg));

        System.out.println("\n[6] Function<Student,String> formatter:");
        reportService.formatStudents(all,
                s -> String.format("[%s] %s - %s", s.getStudentId(), s.getFullName(), s.getMajor().getCode()))
                .forEach(line -> System.out.println("  " + line));

        System.out.println("\n[7] Supplier<Integer> student count:");
        var supplier = reportService.studentCountSupplier(all);
        System.out.println("  Supplier.get() -> " + supplier.get() + " students");
        pause();
    }

    // Option 12 — Concurrency
    private static void demonstrateBatchProcessing() {
        System.out.println("=".repeat(70));
        System.out.println("BATCH PROCESSING - ExecutorService + Callable (US5)");
        System.out.println("=".repeat(70));
        if (courses.isEmpty()) { System.out.println("No courses available."); return; }

        System.out.println("  Processing " + courses.size() + " courses concurrently...");
        long start = System.currentTimeMillis();
        List<BatchProcessingService.CourseProcessingResult> results =
                batchService.processAllCourses(courses);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("  Results:");
        System.out.println("  " + "-".repeat(60));
        for (var r : results) {
            System.out.printf("  Course: %-8s | Students: %2d | Avg GPA: %.2f | %s%n",
                    r.courseCode(), r.studentCount(), r.averageGpa(), r.status());
        }
        System.out.println("  " + "-".repeat(60));
        System.out.println("  " + results.size() + " courses processed in " + elapsed + " ms");

        List<Student> all = studentService.getAllStudents();
        if (!all.isEmpty()) {
            System.out.println("\n  Generating transcripts concurrently...");
            Map<String, String> transcripts = batchService.generateTranscriptsConcurrently(all);
            System.out.println("  Generated " + transcripts.size() + " transcript(s) in parallel.");
        }
        pause();
    }

    // Option 13 — NIO2 Export
    private static void demonstrateNio2Export() {
        System.out.println("=".repeat(70));
        System.out.println("NIO2 EXPORT TRANSCRIPTS (US6)");
        System.out.println("=".repeat(70));
        List<Student> all = studentService.getAllStudents();
        if (all.isEmpty()) { System.out.println("No students to export."); return; }

        System.out.println("  Exporting " + all.size() + " transcript(s) to data/transcripts/...");
        int written = fileService.exportAllTranscripts(all);
        System.out.println("  " + written + " file(s) written using Files.newBufferedWriter(Path)");

        System.out.println("\n  Listing exported files (Files.newDirectoryStream):");
        fileService.listExportedTranscripts()
                .forEach(name -> System.out.println("    " + name));

        System.out.println("\n  Reading back first transcript (Files.readAllLines):");
        System.out.println(fileService.readTranscript(all.get(0).getStudentId()));
        pause();
    }

    // Option 14 — NIO2 Import
    private static void demonstrateNio2Import() {
        System.out.println("=".repeat(70));
        System.out.println("NIO2 IMPORT STUDENTS FROM CSV (US6)");
        System.out.println("=".repeat(70));
        System.out.println("  Reading data/students.csv using Files.newBufferedReader(Path)...");
        List<Student> imported = fileService.importStudentsFromCsv();

        if (imported.isEmpty()) {
            System.out.println("  No students imported (ensure data/students.csv exists).");
        } else {
            System.out.println("  Imported " + imported.size() + " student(s):");
            imported.forEach(s -> System.out.printf("  %-20s | %s%n", s.getFullName(), s.getMajor().getFullName()));
            System.out.print("\n  Add these students to the system? (y/n): ");
            if ("y".equalsIgnoreCase(scanner.nextLine().trim())) {
                imported.forEach(studentService::addStudent);
                System.out.println("  " + imported.size() + " student(s) added.");
            }
        }
        pause();
    }

    // Option 15 — Localisation
    private static void demonstrateLocalisation() {
        System.out.println("=".repeat(70));
        System.out.println("LOCALISATION DEMO (US7)");
        System.out.println("=".repeat(70));

        System.out.println("\n[1] GPA 3.75 formatted in 3 locales (NumberFormat):");
        localeService.printMultiLocaleGpa(3.75);

        System.out.println("\n[2] Today's date in 3 locales (DateTimeFormatter.ofLocalizedDate):");
        localeService.printMultiLocaleDate(LocalDate.now());

        System.out.println("\n[3] ResourceBundle UI labels per locale:");
        for (int i = 1; i <= 3; i++) {
            Locale locale = localeService.fromMenuChoice(i);
            System.out.println("  " + localeService.localeMenu().get(i) + ":");
            System.out.println("    GPA label     : " + localeService.getMessage("label.gpa", locale));
            System.out.println("    Enrolled label: " + localeService.getMessage("label.enrolled", locale));
            System.out.println("    Major label   : " + localeService.getMessage("label.major", locale));
        }

        List<Student> all = studentService.getAllStudents();
        if (!all.isEmpty()) {
            Student s = all.get(0);
            System.out.println("\n[4] Full locale record for " + s.getFullName() + ":");
            for (int i = 1; i <= 3; i++) {
                System.out.println();
                localeService.printLocalisedRecord(s, localeService.fromMenuChoice(i));
            }
        }
        pause();
    }

    // Option 16 — Java 25 Extras
    private static void demonstrateJava25Extras() {
        System.out.println("=".repeat(70));
        System.out.println("JAVA 25 EXTRAS - ScopedValue + Stream Gatherers (US10-US11)");
        System.out.println("=".repeat(70));

        // --- Scoped Values ---
        System.out.println("\n[1] ScopedValue (Java 25 JEP 487)");
        System.out.println("  In Java 25, ScopedValue.where(KEY, value).run(action) binds");
        System.out.println("  a value for the duration of a scope — no ThreadLocal.remove() needed.");
        System.out.println();
        System.out.print("  Before scope — admin  : ");
        System.out.println(ScopedContext.currentAdmin());

        ScopedContext.runAs("admin@tus.ie", "ENROLL", () -> {
            System.out.print("  Inside scope  — admin : ");
            ScopedContext.printContext();
            System.out.println("  Nested call sees     : admin=" + ScopedContext.currentAdmin());
        });

        System.out.print("  After scope   — admin : ");
        System.out.println(ScopedContext.currentAdmin());

        // --- Stream Gatherers ---
        System.out.println("\n[2] Stream Gatherers (Java 25 JEP 485)");
        System.out.println("  Gatherers are stateful intermediate stream operations with:");
        System.out.println("    initializer() - creates mutable state per stream");
        System.out.println("    integrator()  - processes each element");
        System.out.println("    finisher()    - emits any remaining buffered elements");
        System.out.println("  They generalise map/filter/flatMap for custom transformations.");
        System.out.println();

        List<Student> all = studentService.getAllStudents();
        if (all.isEmpty()) {
            System.out.println("  No students — import some first (option 14).");
            pause();
            return;
        }

        System.out.println("  windowBySize(3) — groups students into pages of 3:");
        StudentGatherers.printPages(all, 3);

        System.out.println("\n  runningAverageGpa() — stateful running average:");
        StudentGatherers.printRunningAverage(all);
        pause();
    }

    // Option 17 — JEP 513
    private static void demonstrateJep513() {
        System.out.println("=".repeat(70));
        System.out.println("JAVA 25 JEP 513 - Flexible Constructor Bodies (US9)");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("  Before JEP 513: super() HAD to be the very first statement.");
        System.out.println("  After  JEP 513: statements allowed BEFORE super(), as long as");
        System.out.println("                 they do not access 'this'.");
        System.out.println();

        System.out.println("  [A] Valid Student — name normalised before super():");
        Student s = new Student("  alice  ", "murphy", "Alice.Murphy@TUS.IE",
                DepartmentType.COMPUTER_SCIENCE);
        System.out.println("    Input  : '  alice  ' + 'murphy'");
        System.out.println("    Stored : '" + s.getFirstName() + "' + '" + s.getLastName() + "'");
        System.out.println("    Email  : '" + s.getEmail() + "'  (lowercased pre-super)");

        System.out.println();
        System.out.println("  [B] Invalid Student — blank name rejected BEFORE super() runs:");
        try {
            new Student("", "Murphy", "x@tus.ie", DepartmentType.MATHEMATICS);
        } catch (IllegalArgumentException e) {
            System.out.println("    Caught: " + e.getMessage());
        }

        System.out.println();
        System.out.println("  [C] Valid Instructor — email normalised before super():");
        Instructor inst = new Instructor("Dr. Sarah", "Johnson",
                "S.JOHNSON@TUS.IE", DepartmentType.COMPUTER_SCIENCE, 75000);
        System.out.println("    Email stored : '" + inst.getEmail() + "'  (lowercased pre-super)");

        System.out.println();
        System.out.println("  [D] Invalid Instructor — negative salary rejected before super():");
        try {
            new Instructor("Prof", "Bad", "bad@tus.ie", DepartmentType.MATHEMATICS, -1000);
        } catch (IllegalArgumentException e) {
            System.out.println("    Caught: " + e.getMessage());
        }
        pause();
    }

    // =========================================================================
    // OOP1 METHODS
    // =========================================================================

    private static void addStudent() {
        System.out.println("--- Add New Student ---");
        System.out.print("First name: "); String firstName = scanner.nextLine().trim();
        System.out.print("Last name: ");  String lastName  = scanner.nextLine().trim();
        System.out.print("Email: ");      String email     = scanner.nextLine().trim();
        if (!ValidationUtil.isValidEmail(email)) { System.out.println("Invalid email!"); return; }
        System.out.println("\nSelect Major:");
        DepartmentType[] depts = DepartmentType.values();
        for (int i = 0; i < depts.length; i++) System.out.println((i+1) + ". " + depts[i].getFullName());
        System.out.print("Choice (1-" + depts.length + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > depts.length) { System.out.println("Invalid choice!"); return; }
            Student student = new Student(firstName, lastName, email, depts[choice - 1]);
            studentService.addStudent(student);
            System.out.println("Student added! ID: " + student.getStudentId());
        } catch (NumberFormatException e) { System.out.println("Invalid input!"); }
    }

    private static void viewAllStudents() {
        List<Student> students = studentService.getAllStudents();
        System.out.println("ALL STUDENTS (" + students.size() + ")");
        System.out.println("=".repeat(70));
        if (students.isEmpty()) System.out.println("No students yet.");
        else students.forEach(s -> System.out.println(s));
    }

    private static void searchStudent() {
        System.out.print("Search by name: ");
        String query = scanner.nextLine().trim().toLowerCase();
        List<Student> found = studentService.getAllStudents().stream()
                .filter(s -> s.getFirstName().toLowerCase().contains(query) ||
                             s.getLastName().toLowerCase().contains(query))
                .toList();
        if (found.isEmpty()) System.out.println("No students found.");
        else { System.out.println("Found " + found.size() + ":"); found.forEach(s -> System.out.println("  " + s)); }
    }

    private static void enrollStudent() {
        System.out.print("Student ID: "); String sid = scanner.nextLine().trim();
        Student student = findStudent(sid);
        if (student == null) { System.out.println("Student not found!"); return; }
        System.out.print("Course Code: "); String code = scanner.nextLine().trim().toUpperCase();
        Course course = findCourse(code);
        if (course == null) { System.out.println("Course not found!"); return; }
        try {
            enrollmentService.enrollStudent(student, course);
            System.out.println("Enrolled " + student.getFullName() + " in " + course.getCourseName());
        } catch (EnrollmentException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void assignGrade() {
        System.out.print("Student ID: "); String sid = scanner.nextLine().trim();
        Student student = findStudent(sid);
        if (student == null) { System.out.println("Student not found!"); return; }
        System.out.print("Course Code: "); String code = scanner.nextLine().trim().toUpperCase();
        Grade[] grades = Grade.values();
        for (int i = 0; i < grades.length; i++) System.out.println((i+1) + ". " + grades[i] + " (" + grades[i].getGradePoint() + ")");
        System.out.print("Choice: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > grades.length) { System.out.println("Invalid grade!"); return; }
            enrollmentService.assignGrade(sid, code, grades[choice - 1]);
            student.addGrade(grades[choice - 1]);
            System.out.println("Grade " + grades[choice-1] + " assigned. GPA: " + String.format("%.2f", student.getGPA()));
        } catch (NumberFormatException e) { System.out.println("Invalid input!");
        } catch (EnrollmentException e)   { System.out.println("Error: " + e.getMessage()); }
    }

    private static void viewTranscript() {
        System.out.print("Student ID: "); String sid = scanner.nextLine().trim();
        Student student = findStudent(sid);
        if (student == null) { System.out.println("Student not found!"); return; }
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(sid);
        System.out.println(Transcript.createFromStudent(student, enrollments).generateTranscriptReport());
    }

    private static void viewAllCourses() {
        System.out.println("ALL COURSES (" + courses.size() + ")");
        courses.forEach(c -> {
            System.out.println(c);
            if (c.getInstructor() != null) System.out.println("  Instructor: " + c.getInstructor().getFullName());
        });
    }

    private static void viewReports() {
        System.out.println("1. Honour Roll  2. Students by Major  3. Instructors");
        System.out.print("Choice: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1 -> { studentService.findHonorRollStudents()
                        .forEach(s -> System.out.println(s.getFullName() + " - " + String.format("%.2f", s.getGPA()))); }
                case 2 -> { for (DepartmentType d : DepartmentType.values())
                        System.out.println(d.getFullName() + ": " + studentService.findByMajor(d).size()); }
                case 3 -> instructors.forEach(i -> System.out.println(i));
            }
        } catch (NumberFormatException e) { System.out.println("Invalid input!"); }
    }

    private static void demonstrateOOP1() {
        System.out.println("=".repeat(70));
        System.out.println("OOP1 FEATURES DEMONSTRATION");
        System.out.println("=".repeat(70));
        Student s1 = new Student("Demo", "Student", "demo@tus.ie", DepartmentType.COMPUTER_SCIENCE);
        Instructor i1 = new Instructor("Demo", "Prof", "prof@tus.ie", DepartmentType.MATHEMATICS, 70000);
        Person[] people = {s1, i1};
        System.out.println("\n1. Sealed class polymorphism:");
        for (Person p : people) System.out.println("   " + p.getFullName() + " - " + p.getRole());
        System.out.println("\n2. Enums: Grade.A = " + Grade.A.getGradePoint() + " pts");
        s1.addGrade(Grade.A, Grade.A_MINUS, Grade.B_PLUS);
        System.out.println("3. Varargs addGrade: " + s1.getGrades());
        System.out.println("4. LVTI: var name = \"" + s1.getFullName() + "\"");
        CourseSchedule sched = new CourseSchedule("Monday", LocalTime.of(9, 0), LocalTime.of(10, 30), "Room 101");
        System.out.println("5. Record: " + sched.getFormattedSchedule());
        System.out.println("6. Switch expression: " + GradeUtil.getGradeDescription(Grade.A));
        System.out.println("7. Pattern matching: " + GradeUtil.describeGradeValue(Grade.A));
        List<Grade> copy = s1.getGrades(); copy.add(Grade.F);
        System.out.println("8. Defensive copy: after modifying copy -> " + s1.getGrades() + " (unchanged)");
        Predicate<Student> highGPA = st -> st.getGPA() >= 3.5;
        System.out.println("9. Predicate lambda: on honour roll = " + highGPA.test(s1));
        pause();
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private static void initializeSampleData() {
        Instructor inst1 = new Instructor("Dr. Sarah", "Johnson", "s.johnson@tus.ie",
                DepartmentType.COMPUTER_SCIENCE, 75000);
        inst1.setOfficeHours("Monday 2-4pm", "Wednesday 3-5pm");
        instructors.add(inst1);
        Instructor inst2 = new Instructor("Prof. Michael", "Chen", "m.chen@tus.ie",
                DepartmentType.MATHEMATICS, 80000);
        inst2.setOfficeHours("Tuesday 1-3pm");
        instructors.add(inst2);

        Course cs101 = new Course("CS101", "Introduction to Programming", DepartmentType.COMPUTER_SCIENCE, 3, 30);
        cs101.setInstructor(inst1);
        cs101.setSchedule(new CourseSchedule("Monday", LocalTime.of(9, 0), LocalTime.of(10, 30), "Room 101"));
        courses.add(cs101);
        Course cs201 = new Course("CS201", "Data Structures", DepartmentType.COMPUTER_SCIENCE, 4, 25);
        cs201.setInstructor(inst1); cs201.setPrerequisites("CS101");
        cs201.setSchedule(new CourseSchedule("Wednesday", LocalTime.of(11, 0), LocalTime.of(12, 30), "Room 102"));
        courses.add(cs201);
        Course math101 = new Course("MATH101", "Calculus I", DepartmentType.MATHEMATICS, 4, 35);
        math101.setInstructor(inst2);
        math101.setSchedule(new CourseSchedule("Tuesday", LocalTime.of(10, 0), LocalTime.of(11, 30), "Room 201"));
        courses.add(math101);

        // Sample students with grades pre-loaded for demo
        Object[][] data = {
            {"Emma",  "Walsh",  "e.walsh@student.tus.ie",  DepartmentType.COMPUTER_SCIENCE, new Grade[]{Grade.A,      Grade.A_MINUS, Grade.B_PLUS}},
            {"Oisin", "Byrne",  "o.byrne@student.tus.ie",  DepartmentType.MATHEMATICS,      new Grade[]{Grade.B,      Grade.B_MINUS}},
            {"Ciara", "Murphy", "c.murphy@student.tus.ie", DepartmentType.COMPUTER_SCIENCE, new Grade[]{Grade.A_PLUS, Grade.A}},
            {"Tadhg", "Kelly",  "t.kelly@student.tus.ie",  DepartmentType.ENGINEERING,      new Grade[]{Grade.C_PLUS, Grade.C}},
            {"Niamh", "Ryan",   "n.ryan@student.tus.ie",   DepartmentType.BUSINESS,         new Grade[]{Grade.B_PLUS, Grade.B}},
        };
        for (Object[] row : data) {
            Student s = new Student((String)row[0], (String)row[1], (String)row[2], (DepartmentType)row[3]);
            s.addGrade((Grade[])row[4]);
            studentService.addStudent(s);
            try { cs101.enrollStudent(s); } catch (Exception ignored) {}
        }
        System.out.println("System initialised — " + courses.size() + " courses, "
                + studentService.getStudentCount() + " students");
    }

    private static Student findStudent(String id) {
        return studentService.getAllStudents().stream()
                .filter(s -> s.getStudentId().equals(id)).findFirst().orElse(null);
    }

    private static Course findCourse(String code) {
        return courses.stream()
                .filter(c -> c.getCourseCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }

    private static void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
