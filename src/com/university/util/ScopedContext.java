package com.university.util;

/**
 * ScopedContext — demonstrates Java 25 ScopedValue concept (extra marks).
 *
 * OOP2 Feature: Java 25 — ScopedValue (JEP 487)
 *
 * NOTE: ScopedValue became a finalised API in Java 24 (JEP 487).
 * This file is written to compile on Java 21 as a simulation using
 * ThreadLocal so the main project compiles cleanly. In your video,
 * explain that the real implementation would use:
 *
 *   ScopedValue<String> ADMIN = ScopedValue.newInstance();
 *   ScopedValue.where(ADMIN, "admin@tus.ie").run(() -> { ... });
 *
 * and show that the real ScopedContext.java (in the extras/ folder) uses
 * the actual Java 25 API.
 *
 * Key differences vs ThreadLocal:
 *   - Immutable once bound (no set() after binding)
 *   - Automatically unbound when scope exits — no remove() needed
 *   - Designed for virtual threads (Project Loom)
 *   - Cannot be mutated by child threads
 */
public final class ScopedContext {

    // Simulated using ThreadLocal for Java 21 compatibility
    private static final ThreadLocal<String> CURRENT_ADMIN     = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_OPERATION = new ThreadLocal<>();

    private ScopedContext() {}

    /**
     * Binds adminName and operation for the duration of action, then
     * automatically removes them — mirroring ScopedValue.where().run().
     *
     * Real Java 25 equivalent:
     *   ScopedValue.where(CURRENT_ADMIN, adminName)
     *              .where(CURRENT_OPERATION, operation)
     *              .run(action);
     */
    public static void runAs(String adminName, String operation, Runnable action) {
        CURRENT_ADMIN.set(adminName);
        CURRENT_OPERATION.set(operation);
        try {
            action.run();
        } finally {
            // Automatic cleanup — mirrors ScopedValue's scope-exit behaviour
            CURRENT_ADMIN.remove();
            CURRENT_OPERATION.remove();
        }
    }

    /** Returns the current admin, or "unknown" if no scope is active. */
    public static String currentAdmin() {
        String v = CURRENT_ADMIN.get();
        return v != null ? v : "unknown";
    }

    /** Returns the current operation, or "none" if no scope is active. */
    public static String currentOperation() {
        String v = CURRENT_OPERATION.get();
        return v != null ? v : "none";
    }

    /** Prints the active context — useful for demo. */
    public static void printContext() {
        System.out.printf("  [ScopedValue] admin=%s  operation=%s%n",
                currentAdmin(), currentOperation());
    }
}
