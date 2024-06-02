package lol.nikko.ectasyinjector.logging;

public class Logger {
    private final boolean debug;

    public Logger(boolean debug) {
        this.debug = debug;
    }

    public void info(String message) {
        System.out.println("[Info] " + message);
    }

    public void warn(String message) {
        System.out.println("[Warning] " + message);
    }

    public void error(String message) {
        System.err.println("[Error] " + message);
    }

    public void debug(String message) {
        if (debug)
            System.out.println("[Debug] " + message);
    }
}
