#!/bin/bash


# 1
# Создать папку в корневой HDFS-папке
hdfs dfs -mkdir /folder

# Создать в созданной папке новую вложенную папку
hdfs dfs -mkdir /folder/nested_folder

# Что такое Trash в распределенной FS? Как сделать так, чтобы файлы удалялись сразу, минуя "Trash"?
# Hadoop предоставляет папку trash для сохранения удаленных из HDFS файлов, которые в течении заданного времени (например, 4 дней) могут быть восстановлены. 
# Это полезно, например, в том случае, если вы случайно удалили нужный вам файл.
# Для того, чтобы файлы удалились полностью, минуя папку .trash, необходимо использовать -skipTrash: hdfs dfs -rm -skipTrash <FILE>


# Создать пустой файл в подпапке из пункта 2
hdfs dfs -touchz /folder/nested_folder/empty_file.txt

# Удалить созданный файл
hdfs dfs -rm /folder/nested_folder/empty_file.txt

# Удалить созданные папки
hdfs dfs -rm -r /folder


# 2
# Скопировать любой файл в новую папку на HDFS
hdfs dfs -mkdir -p /user/geranium/
hdfs dfs -put myrobot.xacro /user/geranium


# Вывести содержимое HDFS-файла на экран
hdfs dfs -cat /user/geranium/myrobot.xacro

# Вывести содержимое нескольких последних строчек HDFS-файла на экран
hdfs dfs -cat /user/geranium/myrobot.xacro | tail -5

# Вывести содержимое нескольких первых строчек HDFS-файла на экран
hdfs dfs -cat /user/geranium/myrobot.xacro | head -5

# Переместить копию файла в HDFS на новую локацию
hdfs dfs -cp /user/geranium/myrobot.xacro /user


# 3
# Изменить replication factor для файла. Как долго занимает время на увеличение / уменьшение числа реплик для файла?
# Заняло около 5 секунд на уменьшение числа реплик с 3 до 2 и около 3 секунд на увеличение числа реплик обратно.
hdfs dfs -setrep -w 2 /user/myrobot.xacro

# Найти информацию по файлу, блокам и их расположениям с помощью hdfs fsck
hdfs fsck /user/myrobot.xacro
hdfs fsck /user/myrobot.xacro -files -blocks

# Получить информацию по любому блоку из п.2 с помощью "hdfs fsck -blockId”. Обратить внимание на Generation Stamp (GS number).
hdfs fsck -blockId blk_1073741847

