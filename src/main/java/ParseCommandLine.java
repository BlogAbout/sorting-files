import org.apache.commons.cli.*;

import java.nio.charset.Charset;
import java.util.List;

public class ParseCommandLine {
    private String[] args;

    public ParseCommandLine(String[] args) {
        this.args = args;
    }

    public void parse() {
        Options options = new Options();
        options.addOption("s", false, "Файлы содержат строки. Обязательно. Взаимоисключающая с -i.");
        options.addOption("i", false, "Файлы содержат целые числа. Обязательно. Взаимоисключащая с -s.");
        options.addOption("a", false, "Сортировка по возрастанию. Не обязательно. Взаимоисключаюющая с -d.");
        options.addOption("d", false, "Сортировка по убыванию. Не обязательно. Взаимоисключающая с -a.");
        options.addOption("w", false, "Файл в кодировке CP1251. Не обязательно. По умолчанию UTF8.");
        options.addOption("h", false, "Показать справку.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            System.err.printf("Неизвестная опция %s", e.getOption());
            usagePrintAndShutdown(options, 1);
        } catch (ParseException e) {
            System.err.printf("Сбой разбора командной строки %s", e.getMessage());
            usagePrintAndShutdown(options, 2);
        }

        if (cmd == null) {
            System.err.println("Сбой разбора командной строки по неведомой причине");
            usagePrintAndShutdown(options, 3);
        } else {
            if (cmd.hasOption('h') || args.length == 0) {
                usagePrintAndShutdown(options, 0);
            }
            if (!(cmd.hasOption('i') || cmd.hasOption('s'))) {
                System.err.println("Отсутствует обязательная опция -s или -i");
                usagePrintAndShutdown(options, 4);
            }
            if (cmd.hasOption('i') && cmd.hasOption('s')) {
                System.err.println("Должна быть только одна опция или -s или -i");
                usagePrintAndShutdown(options, 5);
            }
            if (cmd.hasOption('a') && cmd.hasOption('d')) {
                System.err.println("Должна быть только одна опция или -a или -d");
                usagePrintAndShutdown(options, 6);
            }

            List<String> files = cmd.getArgList();
            if (files.size() < 2) {
                System.err.println("Отсутствует имя файла для результата, или хотя бы одно имя входного файла.");
                usagePrintAndShutdown(options, 7);
            }

            if (cmd.hasOption('d'))
                Init.isAsc = false;

            if (cmd.hasOption('i'))
                Init.isString = false;

            if (cmd.hasOption('w'))
                Init.encoding = Charset.forName("cp1251");

            Init.outputFileName = files.get(0);
            files.remove(0);
            Init.inputFileNames = files;
        }
    }

    private void usagePrint(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar sorting-files-jar-with-dependencies.jar [OPTIONS] output.file input.files...\n" +
                "output.file Обязательное имя файла с результатом сортировки.\n" +
                "input.files Один или более входных файлов.\n", options);
    }

    private void usagePrintAndShutdown(Options options, int status) {
        usagePrint(options);
        System.exit(status);
    }
}