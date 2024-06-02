package lol.nikko.ectasyinjector.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtil {
    public static String getStackTrace(Throwable throwable) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            pw.close();
            sw.close();
            return sw.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
