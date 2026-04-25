package com.university.service;

import com.university.model.DepartmentType;
import com.university.model.Grade;
import com.university.model.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP2 Feature: NIO2 — Path, Files, BufferedWriter, BufferedReader, DirectoryStream.
 */
public class FileService {

    private static final Path TRANSCRIPT_DIR = Paths.get("data", "transcripts");
    private static final Path STUDENT_CSV    = Paths.get("data", "students.csv");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public int exportAllTranscripts(List<Student> students) {
        ensureDir();
        int written = 0;
        for (Student s : students) {
            Path p = TRANSCRIPT_DIR.resolve(s.getStudentId() + "_transcript.txt");
            try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                w.write("=".repeat(50)); w.newLine();
                w.write("TRANSCRIPT — " + s.getFullName()); w.newLine();
                w.write("=".repeat(50)); w.newLine();
                w.write("Student ID : " + s.getStudentId()); w.newLine();
                w.write("Major      : " + s.getMajor().getFullName()); w.newLine();
                w.write("GPA        : " + String.format("%.2f", s.getGPA())); w.newLine();
                w.write("Enrolled   : " + s.getEnrollmentDate().format(FMT)); w.newLine();
                w.write("Status     : " + (s.isActive() ? "Active" : "Inactive")); w.newLine();
                w.newLine(); w.write("Grades:"); w.newLine();
                for (Grade g : s.getGrades()) { w.write("  " + g + " (" + g.getGradePoint() + ")"); w.newLine(); }
                w.write("Generated  : " + LocalDate.now().format(FMT)); w.newLine();
                written++;
            } catch (IOException e) {
                System.err.println("  [FileService] Failed to write: " + e.getMessage());
            }
        }
        return written;
    }

    public boolean exportTranscript(Student s) {
        ensureDir();
        Path p = TRANSCRIPT_DIR.resolve(s.getStudentId() + "_transcript.txt");
        List<String> lines = new ArrayList<>();
        lines.add("=".repeat(50));
        lines.add("TRANSCRIPT — " + s.getFullName());
        lines.add("Student ID : " + s.getStudentId());
        lines.add("GPA        : " + String.format("%.2f", s.getGPA()));
        lines.add("Generated  : " + LocalDate.now().format(FMT));
        try {
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) { return false; }
    }

    public List<Student> importStudentsFromCsv() { return importStudentsFromCsv(STUDENT_CSV); }

    public List<Student> importStudentsFromCsv(Path csvPath) {
        List<Student> imported = new ArrayList<>();
        if (!Files.exists(csvPath)) {
            System.err.println("  [FileService] CSV not found: " + csvPath.toAbsolutePath());
            return imported;
        }
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line; int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                if (parts.length < 4) { System.err.println("  [FileService] Skipping line " + lineNum); continue; }
                try {
                    DepartmentType dept = parseDept(parts[3].trim());
                    imported.add(new Student(parts[0].trim(), parts[1].trim(), parts[2].trim(), dept));
                } catch (IllegalArgumentException e) {
                    System.err.println("  [FileService] Bad data line " + lineNum + ": " + e.getMessage());
                }
            }
        } catch (IOException e) { System.err.println("  [FileService] Read failed: " + e.getMessage()); }
        return imported;
    }

    public List<String> listExportedTranscripts() {
        List<String> names = new ArrayList<>();
        if (!Files.exists(TRANSCRIPT_DIR)) return names;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(TRANSCRIPT_DIR, "*_transcript.txt")) {
            for (Path entry : stream) names.add(entry.getFileName().toString());
        } catch (IOException e) { System.err.println("  [FileService] List failed: " + e.getMessage()); }
        names.sort(String::compareTo);
        return names;
    }

    public String readTranscript(String studentId) {
        Path p = TRANSCRIPT_DIR.resolve(studentId + "_transcript.txt");
        if (!Files.exists(p)) return "Transcript not found for: " + studentId;
        try {
            return String.join(System.lineSeparator(), Files.readAllLines(p, StandardCharsets.UTF_8));
        } catch (IOException e) { return "Error reading: " + e.getMessage(); }
    }

    private void ensureDir() {
        try { Files.createDirectories(TRANSCRIPT_DIR); }
        catch (IOException e) { System.err.println("  [FileService] Cannot create dir: " + e.getMessage()); }
    }

    private DepartmentType parseDept(String code) {
        for (DepartmentType d : DepartmentType.values()) {
            if (d.getCode().equalsIgnoreCase(code) || d.name().equalsIgnoreCase(code)) return d;
        }
        throw new IllegalArgumentException("Unknown department: " + code);
    }
}
