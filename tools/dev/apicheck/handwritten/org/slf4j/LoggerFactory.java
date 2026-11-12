package org.slf4j;

public final class LoggerFactory {
    private LoggerFactory() {}

    public static Logger getLogger(String name) {
        return new SimpleLogger(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    private static final class SimpleLogger implements Logger {
        private final String name;
        SimpleLogger(String name) { this.name = name; }
        @Override public String getName() { return name; }
        @Override public void trace(String msg) {}
        @Override public void trace(String format, Object... arguments) {}
        @Override public void debug(String msg) {}
        @Override public void debug(String format, Object... arguments) {}
        @Override public void info(String msg) {}
        @Override public void info(String format, Object... arguments) {}
        @Override public void warn(String msg) {}
        @Override public void warn(String format, Object... arguments) {}
        @Override public void error(String msg) {}
        @Override public void error(String format, Object... arguments) {}
        @Override public void error(String msg, Throwable t) {}
    }
}
