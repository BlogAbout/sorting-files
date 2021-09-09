import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import java.io.*;
import java.util.stream.Stream;

public class Sorter {
    private final String sp = System.getProperty("line.separator");

    /**
     * <p>Сортировка строк соглавно аргументам командной строки.</p>
     * <p>Вызывается из метода merge(destination, source1, source2).</p>
     * @param left - строка из первого файла
     * @param right - строка из второго файла
     * @return true если левая строка меньше (при сортировке по возрастанию) / больше (при сортировке по убыванию), чем правая строка; false - если наоборот
     */
    private boolean sort(String left, String right) {
        if (Init.isString) {
            if (Init.isAsc)
                return left.compareTo(right) < 0;
            else
                return left.compareTo(right) > 0;
        } else {
            int leftInt = stringToInteger(left);
            int rightInt = stringToInteger(right);

            if (Init.isAsc)
                return leftInt < rightInt;
            else
                return leftInt > rightInt;
        }
    }

    /**
     * <p>Слияние файлов с сортировкой.</p>
     * <p>Итераторы каждого из входных файлов получают и сравнивают значения между собой.</p>
     * <p>После чего сохраняют в файл назначения в заданном порядке сортировки.</p>
     * @param destination - файл назначения, куда сохраняются результаты сортировки из исходных файлов
     * @param left - входной файл, откуда берутся значения для сортировки
     * @param right - входной файл, откуда берутся значения для сортировки
     */
    public void merge(File destination, File left, File right) {
        try (
                Stream<String> leftStream = new BufferedReader(new FileReader(left, Init.encoding)).lines();
                Stream<String> rightStream = new BufferedReader(new FileReader(right, Init.encoding)).lines();
                FileWriter writer = new FileWriter(destination, Init.encoding, true)
        ) {
            String leftLine;
            String rightLine;
            PeekingIterator<String> leftPeekingIterator = Iterators.peekingIterator(leftStream.iterator());
            PeekingIterator<String> rightPeekingIterator = Iterators.peekingIterator(rightStream.iterator());

            while(leftPeekingIterator.hasNext() && rightPeekingIterator.hasNext()) {
                leftLine = leftPeekingIterator.peek();
                rightLine = rightPeekingIterator.peek();

                if (sort(leftLine, rightLine)) {
                    writer.append(leftLine).append(sp);
                    leftPeekingIterator.next();
                } else {
                    writer.append(rightLine).append(sp);
                    rightPeekingIterator.next();
                }
            }

            while(leftPeekingIterator.hasNext()) {
                writer.append(leftPeekingIterator.next()).append(sp);
            }

            while(rightPeekingIterator.hasNext()) {
                writer.append(rightPeekingIterator.next()).append(sp);
            }
        } catch (FileNotFoundException e) {
            System.err.printf("Проблема с открытием входного файла по причине %s. Файл исключен.\n", e.getMessage());
        } catch (IOException e) {
            System.err.printf("Проблема с созданием выходного файла по причине %s. Файл исключен.\n", e.getMessage());
        }
    }

    /**
     * <p>Преобразует строку в число.</p>
     * @param string - строка для преобразования
     * @return Integer преобразованное значение
     * @throws NumberFormatException - исключение, в случае ошибки преобразования
     */
    public Integer stringToInteger(String string) throws NumberFormatException {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Нарушение формата чисел в строке '" + string + "'. Причина '" + e.getMessage() + "'.");
        }
    }
}