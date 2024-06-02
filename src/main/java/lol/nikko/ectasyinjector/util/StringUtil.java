package lol.nikko.ectasyinjector.util;

public class StringUtil {
    public static String generateRandom(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append((char) (Math.random() * 26 + 'a'));
        return sb.toString();
    }
}
