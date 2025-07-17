package src.MyCode.model;


import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * You will collect internal URLs (within touro.edu), external URLs (outside of touro.edu), phone numbers,
 * and email addresses. In addition, your group should come up with at least two additional, non-trivial
 * pieces of information to scrape from the touro.edu site using regexes.
 */
public enum ResultType {
    

    PHONE("Phone #", "<a\\s+href=\\\"tel:([^\"]+)"),
    EMAIL("Email Address", "<a\\s+href=\\\"mailto:([^\"]+)"),
    SHORT_PARAGRAPH("Short Paragraph", "<p[^>]*>((?:[^<]){0,24})</p>"),
    HEADING("heading", "<h[1-6][^>]*>(.*?)</h[1-6]>"),
    URL("URL", "<a\\s+[^>]*?href=\"([^\"]+)\"");









    private final String label;
    private final Pattern regex;


    /**
     * Constructor
     * @param label How this type of result will be displayed to the user.
     * @param regex The regex pattern.
     */
    ResultType(String label, String regex) {
        this.label = label;
        this.regex = Pattern.compile(regex);
    }


    public Matcher matcher(String input) {
        return regex.matcher(input);
    }

    public String getLabel() {
        return label;
    }

}
