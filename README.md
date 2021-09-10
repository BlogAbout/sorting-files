# Test task. Sorting merge files.

##### Написать программу сортировки слиянием нескольких файлов.

* Входные файлы содержат данные одного из двух видов: целые числа или строки.
* Данные записаны в столбик (каждая строка файла – новый элемент).
* Строки могут содержать любые не пробельные символы.
* Файлы должны быть предварительно отсортированы.
* Результатом работы программы должен являться новый файл с объединенным содержимым входных файлов, отсортированным по возрастанию или убыванию путем сортировки слиянием.
* Если содержимое исходных файлов не позволяет произвести сортировку слиянием (например, нарушен порядок сортировки), производится частичная сортировка (насколько возможно для этого алгоритма).
* Выходной файл должен содержать отсортированные данные даже в случае ошибок, однако возможна потеря ошибочных данных.

Параметры программы задаются при запуске через аргументы командной строки:
- режим сортировки (-a или -d), необязательный, по умолчанию сортируем по возрастанию;
- тип данных (-s или -i), обязательный;
- имя выходного файла, обязательное;
- остальные параметры – имена входных файлов, не менее одного. 

Примеры запуска из командной строки для Windows:
* `sort-it.exe -i -a out.txt in.txt` (для целых чисел по возрастанию)
* `sort-it.exe -s out.txt in1.txt in2.txt in3.txt` (для строк по возрастанию)
* `sort-it.exe -d -s out.txt in1.txt in2.txt` (для строк по убыванию)


К решению прилагается инструкция по запуску, описанная в файле USAGE.md
На Windows в консольном выводе возможны артефакты из-за проблем с кодировкой.