import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Init {
    public static boolean isAsc = true;
    public static boolean isString = true;
    public static Charset encoding = StandardCharsets.UTF_8;
    public static String outputFileName = "";
    public static List<String> inputFileNames = new ArrayList<>();

    public static void main(String[] args) {
        ParseCommandLine parseCommandLine = new ParseCommandLine(args);
        parseCommandLine.parse();

        Sorter sorter = new Sorter();
        Helper helper = new Helper(sorter);

        helper.prependProcess();
        helper.process();

        helper.removeTmpAllFiles();
    }
}