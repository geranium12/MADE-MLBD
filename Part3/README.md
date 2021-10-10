## Part3

### Условие
1. Загрузите датасет по ценам на жилье Airbnb, доступный на kaggle.com:
https://www.kaggle.com/dgomonov/new-york-city-airbnb-open-data
2. Подсчитайте среднее значение и дисперсию по признаку ”price” стандартными способами (”чистый код” или
использование библиотек). Не учитывайте пропущенные значения при подсчете статистик.
3. Используя Python, реализуйте скрипт mapper.py и reducer.py для расчета каждой из двух величин. В итоге у вас
должно получиться 4 скрипта: 2 mapper и 2 reducer для каждой величины.
4. Проверьте правильность подсчета статистик методом map-reduce в сравнении со стандартным подходом
5. Результаты сравнения (то есть, подсчета двумя разными способами) для среднего значения и дисперсии запишите в
файл .txt. В итоге, у вас должно получиться две пары значений (стандартного расчета и map-reduce)- одна пара для
среднего, другая - для дисперсии.
6. Итоговый результат с выполненным заданием должен включать в себя сам код, а также результаты его работы,
который необходимо разместить в репозитории.

### Результаты
Из исходного файла была оставлена одна колонка price для упрощения чтения файла.

#### Копирование файлов в Docker и HDFS:
```
docker cp ~/Documents/MADE-MLBD/Part3/mapper.py namenode:/
docker cp ~/Documents/MADE-MLBD/Part3/reducer.py namenode:/
docker cp ~/Documents/MADE-MLBD/Part3/data.csv namenode:/
hdfs dfs -put -f /mapper.py /user/root/mapper.py
hdfs dfs -put -f /reducer.py /user/root/reducer.py
hdfs dfs -put -f /data.csv /user/root/data.csv
```

#### Запуск MapReduce:
```
mapred streaming -files mapper.py,reducer.py,data.csv -mapper "python3 mapper.py" -reducer "python3 reducer.py" -input data.csv -output output 
```

#### Просмотр результатов:
```
hdfs dfs -cat /user/root/output/part-00000
```
|               | mean                | variance           |
| ------------- |:-------------------:| :-----------------:|
| numpy         | 152.7110154519201   | 57672.254783037170  |
| hadoop        | 152.7110154519201	  | 57672.254783036784 |
