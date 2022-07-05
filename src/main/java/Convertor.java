import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Convertor {
    public static String stringMatchWith(String str, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public static boolean doesStringMatchWith(String str, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
