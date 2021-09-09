import java.io.*;
import java.util.Iterator;
import java.util.stream.Stream;

public class Helper {
    private String fileNameTmpFiles;
    private String tmpFileNameTmpFiles;
    private final Sorter sorter;
    private final String sp = System.getProperty("line.separator");

    /**
     * <p>Нестатический блок.</p>
     * <p>Происходит создание первого временного файла для хранения списка обрабатываемых в будущем файлов.</p>
     */
    {
        try {
            File file = createTmpFile();
            fileNameTmpFiles = file.getCanonicalPath();
        } catch (IOException e) {
            System.err.printf("Критическая ошибка при создании временного файла по причине %s. Программа будет закрыта.\n", e.getMessage());
            System.exit(10);
        }
    }

    public Helper(Sorter sorter) {
        this.sorter = sorter;
    }

    /**
     * <p>Удаление всех временных файлов, созданных в процессе работы.</p>
     */
    public void removeTmpAllFiles() {
        cleanTmpFiles();
        File file = new File(fileNameTmpFiles);
        //file.deleteOnExit();
        if (file.exists() && !file.delete())
            System.err.printf("Ошибка удаления временного файла %s. Файл пропущен.\n", fileNameTmpFiles);
    }

    /**
     * <p>Подготовительный процесс.</p>
     * <p>Происходит разбор входных файлов из аргументов командной строки.</p>
     * <p>Данные каждого входного файла сохраняются в отдельные временные файлы посимвольно.</p>
     */
    public void prependProcess() {
        if (Init.inputFileNames.size() != 0) {
            try (FileWriter tmpWriterFiles = new FileWriter(fileNameTmpFiles, Init.encoding, true)) {
                for (String fileName : Init.inputFileNames) {
                    try (Stream<String> stream = new BufferedReader(new FileReader(fileName, Init.encoding)).lines()) {
                        int indexIterator = 0;
                        Iterator<String> iterator = stream.iterator();

                        while (iterator.hasNext()) {
                            indexIterator++;
                            String line = iterator.next();
                            if (line.isEmpty() || line.isBlank()) {
                                System.err.printf("Пусто в строке %d файла %s. Строка пропущена.\n", indexIterator, fileName);
                                return;
                            }

                            if (!Init.isString) {
                                try {
                                    int intLine = sorter.stringToInteger(line);
                                } catch (NumberFormatException e) {
                                    System.err.printf(
                                            "В строке %d значение %s не является числом по причине %s. Строка пропущена.\n",
                                            indexIterator,
                                            line,
                                            e.getMessage()
                                    );
                                    return;
                                }
                            }

                            try {
                                File file = createTmpFile();

                                FileWriter writer = new FileWriter(file, Init.encoding);
                                writer.write(line);
                                writer.close();

                                tmpWriterFiles.write(file.getCanonicalPath() + sp);
                            } catch (IOException e) {
                                System.err.printf("Ошибка создания временного файла для записи строки по причине %s.\n", e.getMessage());
                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.err.printf("Ошибка чтения входного файла %s по причине %s. Файл пропущен.\n", fileName, e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.printf("Ошибка открытия временного файла %s по причине %s. Программа будет закрыта.\n", fileNameTmpFiles, e.getMessage());
                System.exit(10);
            }
        }
    }

    /**
     * <p>Основной процесс сортировки слиянием.</p>
     * <p>Вызывается после подготовительного процесса и производит обработку, если подготовленных файлов больше двух.</p>
     * <p>Если количество подготовленных файлов равно двум, то процесс завершится и будет запущен финальный процесс.</p>
     * <p>Если количество подготовленных файлов равно нулю, то программа будет закрыта с ошибкой.</p>
     */
    public void process() {
        boolean isStart = true;

        while(isStart) {
            long count = countLines();
            if (count > 2) {
                try (Stream<String> stream = new BufferedReader(new FileReader(fileNameTmpFiles, Init.encoding)).lines()) {
                    File tmpFiles = createTmpFile();
                    tmpFileNameTmpFiles = tmpFiles.getCanonicalPath();

                    Iterator<String> iterator = stream.iterator();
                    while (iterator.hasNext()) {
                        try {
                            File mergeFile = createTmpFile();
                            String leftFileName = iterator.next();

                            if (iterator.hasNext()) {
                                String rightFileName = iterator.next();
                                sorter.merge(mergeFile.getCanonicalFile(), new File(leftFileName), new File(rightFileName));
                            } else {
                                copyFile(new File(leftFileName), mergeFile);
                            }

                            try (FileWriter writer = new FileWriter(tmpFileNameTmpFiles, Init.encoding, true)) {
                                writer.write(mergeFile.getCanonicalPath() + sp);
                            }
                        } catch (IOException e) {
                            System.err.printf("Ошибка создания временного файла для записи строк по причине %s.\n", e.getMessage());
                        }
                    }

                    swapTmpFiles();
                } catch (FileNotFoundException e) {
                    System.err.printf("Критическая ошибка чтения файла %s по причине %s. Программа будет закрыта.\n", fileNameTmpFiles, e.getMessage());
                    isStart = false;
                } catch (IOException e) {
                    System.err.printf("Ошибка создания временного файла для хранения списка временных файлов %s.\n", e.getMessage());
                }
            } else if (count == 2) {
                finalProcess();
                isStart = false;
            } else {
                System.exit(10);
            }
        }
    }

    /**
     * <p>Финальный процесс сортировки слиянием.</p>
     * <p>Вызывается, когда остается остается 2 обработанных файла и их необходимо слить в выходной файл из аргументов командной строки.</p>
     */
    private void finalProcess() {
        try (Stream<String> stream = new BufferedReader(new FileReader(fileNameTmpFiles, Init.encoding)).lines()) {
            Iterator<String> iterator = stream.iterator();
            while (iterator.hasNext()) {
                File left = new File(iterator.next());
                File right = new File(iterator.next());
                File destination = new File(Init.outputFileName);
                sorter.merge(destination, left, right);
            }
        } catch (IOException e) {
            System.err.printf("Ошибка чтения файла %s по причине %s.\n", fileNameTmpFiles, e.getMessage());
        }
    }

    /**
     * <p>Создание временного файла</p>
     * @return File - объект созданного файла для последующей обработки
     * @throws IOException - исключение, в случае ошибки создания файла
     */
    private File createTmpFile() throws IOException {
        return File.createTempFile("sort", ".part");
    }

    /**
     * <p>Смена временных файлов местами.</p>
     * <p>Происходит каждый раз, когда список файлов из основного файла обработан и требуется перейти к следующему.</p>
     * <p>Перед сменой происходит удаление уже обработанных временных файлов.</p>
     */
    private void swapTmpFiles() {
        cleanTmpFiles();
        fileNameTmpFiles = tmpFileNameTmpFiles;
        tmpFileNameTmpFiles = null;
    }

    /**
     * <p>Копирование содержимого одного файла в другой.</p>
     * <p>Метод необходим на случай нечетного количества строк (файлов) в процессе слияния.</p>
     * @param source входной файл, откуда копируется содержимое
     * @param destination файл назначения, куда копируется содержимое
     */
    private void copyFile(File source, File destination) {
        try (
                Stream<String> stream = new BufferedReader(new FileReader(source, Init.encoding)).lines();
                FileWriter writer = new FileWriter(destination, Init.encoding, true)
        ) {
            Iterator<String> iterator = stream.iterator();
            while (iterator.hasNext()) {
                writer.write(iterator.next() + sp);
            }
        } catch (FileNotFoundException e) {
            System.err.printf("Проблема с открытием входного файла %s по причине %s. Файл исключен.\n", source, e.getMessage());
        } catch (IOException e) {
            System.err.printf("Проблема с созданием выходного файла %s по причине %s. Файл исключен.\n", destination, e.getMessage());
        }
    }

    /**
     * <p>Удаление временных файлов из основного списка временных файлов.</p>
     * <p>В случае ошибки программа продолжит работу, но файл останется в файловой системе.</p>
     */
    private void cleanTmpFiles() {
        try (Stream<String> stream = new BufferedReader(new FileReader(fileNameTmpFiles, Init.encoding)).lines()) {
            Iterator<String> iterator = stream.iterator();
            while (iterator.hasNext()) {
                String currentFileName = iterator.next();
                File tmpFile = new File(currentFileName);
                if (tmpFile.exists() && !tmpFile.delete())
                    System.err.printf("Ошибка удаления временного файла %s. Файл пропущен.\n", currentFileName);
            }
        } catch (IOException e) {
            System.err.printf("Ошибка открытия временного файла для удаления по причине %s. Файл пропущен.\n", e.getMessage());
        }
    }

    /**
     * <p>Подсчёт количества строк в файле для последующей обработки.</p>
     * <p>В случае ошибки вернёт нулевое значение.</p>
     * @return long значение
     */
    private long countLines() {
        try (Stream<String> stream = new BufferedReader(new FileReader(fileNameTmpFiles, Init.encoding)).lines()) {
            return stream.count();
        } catch (IOException e) {
            System.err.printf("Ошибка чтения файла %s для подсчета количества строк по причине %s. Программа будет закрыта.\n", fileNameTmpFiles, e.getMessage());
            return 0L;
        }
    }
}