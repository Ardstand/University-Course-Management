package com.university.service;

import com.university.model.Course;
import com.university.model.Grade;
import com.university.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * OOP2 Feature: Concurrency — ExecutorService, Callable, Future.
 */
public class BatchProcessingService {

    public record CourseProcessingResult(
            String courseCode, int studentCount, double averageGpa, String status) {}

    public List<CourseProcessingResult> processAllCourses(List<Course> courses) {
        int threads = Math.min(courses.size(), Runtime.getRuntime().availableProcessors());
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, threads));

        List<Callable<CourseProcessingResult>> tasks = new ArrayList<>();
        for (Course course : courses) tasks.add(buildTask(course));

        List<CourseProcessingResult> results = new ArrayList<>();
        try {
            List<Future<CourseProcessingResult>> futures = executor.invokeAll(tasks);
            for (Future<CourseProcessingResult> future : futures) {
                try { results.add(future.get()); }
                catch (ExecutionException e) {
                    System.err.println("  [Batch] Task failed: " + e.getCause().getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            try { executor.awaitTermination(10, TimeUnit.SECONDS); }
            catch (InterruptedException e) { executor.shutdownNow(); Thread.currentThread().interrupt(); }
        }
        return results;
    }

    private Callable<CourseProcessingResult> buildTask(Course course) {
        return () -> {
            Thread.sleep(50);
            List<Student> enrolled = course.getEnrolledStudents();
            if (enrolled.isEmpty())
                return new CourseProcessingResult(course.getCourseCode(), 0, 0.0, "SUCCESS (no students)");
            double avgGpa = enrolled.stream()
                    .filter(s -> !s.getGrades().isEmpty())
                    .mapToDouble(Student::getGPA).average().orElse(0.0);
            return new CourseProcessingResult(course.getCourseCode(), enrolled.size(), avgGpa, "SUCCESS");
        };
    }

    public Map<String, String> generateTranscriptsConcurrently(List<Student> students) {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<Map.Entry<String, String>>> futures = new ArrayList<>();
        for (Student student : students) {
            Callable<Map.Entry<String, String>> task = () -> {
                Thread.sleep(20);
                return Map.entry(student.getStudentId(), buildTranscript(student));
            };
            futures.add(executor.submit(task));
        }
        Map<String, String> results = new HashMap<>();
        for (Future<Map.Entry<String, String>> f : futures) {
            try { Map.Entry<String, String> e = f.get(); results.put(e.getKey(), e.getValue()); }
            catch (InterruptedException | ExecutionException e) {
                System.err.println("  [Batch] Transcript failed: " + e.getMessage());
            }
        }
        executor.shutdown();
        return results;
    }

    private String buildTranscript(Student s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Transcript for ").append(s.getFullName()).append(" (").append(s.getStudentId()).append(")\n");
        sb.append("Major : ").append(s.getMajor().getFullName()).append("\n");
        sb.append("GPA   : ").append(String.format("%.2f", s.getGPA())).append("\n");
        sb.append("Grades: ");
        for (Grade g : s.getGrades()) sb.append(g).append(" ");
        return sb.toString();
    }
}
