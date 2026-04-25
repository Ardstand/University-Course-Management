# University Course Management System — OOP2

## How to compile and run

### Standard build (Java 21+)
```bash
find src -name "*.java" > sources.txt
javac -d out -sourcepath src @sources.txt
java -cp out com.university.Main
```

### JEP 512 Compact Source File demo (Java 25 required)
```bash
javac --enable-preview --release 25 QuickDemo.java
java  --enable-preview QuickDemo
```

## OOP2 Features — Menu Map

| Menu | Feature | User Story |
|------|---------|------------|
| 10 | Streams: filter, map, sorted, distinct, limit, min, max, count, findFirst, findAny, allMatch, anyMatch, noneMatch, forEach, collect | US1–US4 |
| 11 | Collectors: groupingBy, partitioningBy, toMap, counting, averagingDouble | US3 |
| 12 | Concurrency: ExecutorService, Callable, Future, invokeAll | US5 |
| 13 | NIO2 Export: Files.newBufferedWriter, Files.newDirectoryStream, Files.readAllLines | US6 |
| 14 | NIO2 Import: Files.newBufferedReader, Files.exists, Path | US6 |
| 15 | Localisation: Locale, NumberFormat, DateTimeFormatter, ResourceBundle | US7 |
| 16 | Java 25: ScopedValue (simulated), Stream Gatherers (simulated) | US10–US11 |
| 17 | Java 25 JEP 513: Flexible Constructor Bodies (documented in code) | US9 |
| —  | Java 25 JEP 512: QuickDemo.java compact source file | US8 |

## Note on Java 25 features

ScopedContext and StudentGatherers demonstrate the **logic and intent** of
Java 25 APIs using Java 21-compatible code. The comments in each file show
the exact Java 25 syntax. QuickDemo.java requires --enable-preview --release 25.
