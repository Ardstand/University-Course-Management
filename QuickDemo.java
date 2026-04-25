/**
 * QuickDemo.java — Java 25 JEP 512: Compact Source Files and Instance Main Methods
 *
 * OOP2 Feature: Java 25 — JEP 512
 *
 * JEP 512 allows:
 *   1. No class declaration wrapper needed
 *   2. main() can be an instance method (no 'static')
 *   3. 'public' and 'void' optional on main()
 *   4. String[] args parameter optional
 *
 * Compile: javac --enable-preview --release 25 QuickDemo.java
 * Run:     java  --enable-preview QuickDemo
 */
void main() {
    System.out.println("=".repeat(60));
    System.out.println("  QuickDemo — Java 25 JEP 512 Compact Source File");
    System.out.println("=".repeat(60));
    System.out.println("  No class declaration. main() is an instance method.");
    System.out.println("  String[] args is omitted. 'public void' is omitted.");
    System.out.println();
    var features = new String[]{
        "Records", "Sealed classes", "Switch + pattern matching",
        "Streams (all terminal + intermediate ops)", "Collectors (groupingBy/partitioningBy/toMap)",
        "Comparator.comparing()", "Consumer, Function, Supplier lambdas",
        "ExecutorService + Callable + Future", "NIO2 (Path, Files, BufferedWriter)",
        "Localisation (Locale, NumberFormat, DateTimeFormatter, ResourceBundle)",
        "ScopedValue (JEP 487)", "Stream Gatherers (JEP 485)",
        "JEP 512 — this file!", "JEP 513 — flexible constructor bodies"
    };
    System.out.println("  OOP2 features in this project:");
    for (int i = 0; i < features.length; i++)
        System.out.printf("    %2d. %s%n", i + 1, features[i]);
    System.out.println();
    System.out.println("  Java: " + System.getProperty("java.version"));
    System.out.println("=".repeat(60));
}
