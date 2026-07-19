package vn.edu.fpt.SE2034_SWP391_G5.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtil {

    public static String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String noWhiteSpace = Pattern.compile("[\\s]").matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]").matcher(normalized).replaceAll("");
        return slug.toLowerCase().replaceAll("[^a-z0-9-]", "").replaceAll("-+", "-");
    }
}
