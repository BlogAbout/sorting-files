#### параметры запуска

```
$ ./target/sorting-files-jar-with-dependencies.jar

usage: java -jar sorting-files-jar-with-dependencies.jar [OPTIONS] output.file input.files...
output.file     Обязательное имя файла с результатом сортировки.
input.files     Один или более входных файлов.
 -a             Сортировка по возрастанию. Применяется по умолчанию при
                отсутствии -a или -d. Взаимоисключающая с -d.
 -d             Сортировка по убыванию. Опция не обязательна как и -a.
                Взаимоисключающая с -a.
 -h             Отобразить справку.
 -i             Файлы содержат целые числа. Обязательна, взаимоисключительна с -s.
 -s             Файлы содержат строки. Обязательна, взаимоисключительна с -i.
 -w             Файлы ожидаются в кодировке CP1251. Опция не обязательна. По
                умолчанию используется UTF8 кодировка файлов.
```
#### примеры запуска

неправильные параметры

```
$ java -jar sorting-files-jar-with-dependencies.jar -a -d -i

Должна быть только одна опция или -a или -d
```

```
$ java -jar sorting-files-jar-with-dependencies.jar -s -i

Должна быть только одна опция или -s или -i
```

```
$ java -jar sorting-files-jar-with-dependencies.jar -i out.txt

Отсутствует имя файла для результата, или хотя бы одно имя входного файла.
```

```
$ java -jar sorting-files-jar-with-dependencies.jar -s out.txt none
Ошибка чтения входного файла none по причине none (Не удается найти указанный файл). Файл пропущен.
```

входные файлы integer ascending
```
$ java -jar sorting-files-jar-with-dependencies.jar -a -i out.txt int1.txt int2.txt int3.txt
В строке 11 файла 'int1.txt' значение 'ASD' не является числом по причине Нарушение формата чисел в строке 'ASD'. Причина 'For input string: "ASD"'.. Строка пропущена.
Пусто в строке 7 файла int3.txt. Строка пропущена.
Сортировка завершена. Результат сохранен в файле out.txt.
-1
-1
0
0
1
1
1
1
1
1
1
1
2
2
3
3
4
4
4
4
5
5
6
6
7
7
8
8
9
9
9
9
16
16
16
27
28
40
64
65
```

входные файлы integer descending
``` 
$ java -jar sorting-files-jar-with-dependencies.jar -d -i out.txt int1.txt int2.txt int3.txt
В строке 11 файла 'int1.txt' значение 'ASD' не является числом по причине Нарушение формата чисел в строке 'ASD'. Причина 'For input string: "ASD"'.. Строка пропущена.
Пусто в строке 7 файла int3.txt. Строка пропущена.
Сортировка завершена. Результат сохранен в файле out.txt.
65
64
40
28
27
16
16
16
9
9
9
9
8
8
7
7
6
6
5
5
4
4
4
4
3
3
2
2
1
1
1
1
1
1
1
1
0
0
-1
-1
```

входные файлы string ascending
```
$ java -jar sorting-files-jar-with-dependencies.jar -a -s out.txt str1.txt str2.txt str3.txt
Сортировка завершена. Результат сохранен в файле C:\Java\out.txt.
1
100A
100A
2
2
2
3
3
3
444
A
A
XXX
XXX
Z
Z
Z
ZZZZ
ZZZZ
ZerrorSorting
a
a
a
aa
aa
aa
aaa
aaa
aaa
b
b
b
bb
bb
bb
bb   bb
bbb
bbb
bbb
bbbb
bbbb
Ф
Х
Ц
а
```


входные файлы string descending
``` 
$ $ java -jar sorting-files-jar-with-dependencies.jar -d -s out.txt str1.txt str2.txt str3.txt
Сортировка завершена. Результат сохранен в файле out.txt.
а
Ц
Х
Ф
bbbb
bbbb
bbb
bbb
bbb
bb   bb
bb
bb
bb
b
b
b
aaa
aaa
aaa
aa
aa
aa
a
a
a
ZerrorSorting
ZZZZ
ZZZZ
Z
Z
Z
XXX
XXX
A
A
444
3
3
3
2
2
2
100A
100A
1
```