package util;

public class DevLogger {
    public static void logError(Exception e) {
        System.err.println("[DEV] " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }
}
