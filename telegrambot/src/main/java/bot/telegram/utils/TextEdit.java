package bot.telegram.utils;

import java.util.HashSet;
import java.util.Set;

public class TextEdit {
    private static final Set<String> markups;

    static {
        markups = new HashSet<>();
        markups.add("<s>");
        markups.add("</s>");
        markups.add("<p>");
        markups.add("</p>");
        markups.add("<span>");
        markups.add("</span>");
        markups.add("&nbsp;");
        markups.add("&amp;");
        markups.add("<br />");
        markups.add("<br>");
        markups.add("<strong>");
        markups.add("</strong>");
        markups.add("<em>");
        markups.add("</em>");
        markups.add("<div class=\"text text-block\">");
        markups.add("</div>");
        markups.add("<div>");
        markups.add("<span style=\"font-size: 0.6em;\">");
        markups.add("<span style=\"font-weight: 700;\">");
    }

    public static boolean hasMarkups(String string) {
        if (string == null)
            return false;
        for (String markup : markups) {
            if (string.contains(markup))
                return true;
        }
        return false;
    }

    public static String removeMarkups(String string) {
        StringBuilder res = new StringBuilder(string);
        for (String markup : markups) {
            if (res.toString().contains(markup)) {
                String repl = "";
                if (markup.equals("&amp;")) {
                    repl = "&";
                } else if (markup.equals("<br>") || markup.equals("<br />")) {
                    repl = "\n";
                }
                res.replace(res.indexOf(markup), res.indexOf(markup) + markup.length(), repl);
            }
        }
        return res.toString();
    }

    public static String pricePrepare(String str) {
        if (str.contains("."))
            str = str.replace(".", "");
        return str;
    }

    public static String betterLookingString(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}