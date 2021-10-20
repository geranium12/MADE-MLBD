## Блок 1. Развертывание локального Hive

### Условие
1. Развернуть локальный Hive в любой конфигурации
2. Подключиться к развернутому Hive с помощью любого инструмента: Hue, Python Driver, Zeppelin, любая IDE и т.д.
3. Сделать скриншоты поднятого Hive и подключений в выбранными вами инструментах, добавить в репозиторий

### Результаты
![hive](./screenshots/hive.png?raw=true)
![beeline](./screenshots/beeline.png?raw=true)
![hue](./screenshots/hue.png?raw=true)


## Блок 2. Работа с Hive
1. Сделать таблицу artists в Hive и вставить туда значения, используя датасет https://www.kaggle.com/pieca111/music-artists-popularity 
2. Используя Hive найти (команды и результаты записать в файл и добавить в репозиторий):
  - Исполнителя с максимальным числом скробблов
 ```
SELECT artist_lastfm, scrobbles_lastfm
FROM artists
WHERE scrobbles_lastfm IN (
    SELECT MAX(scrobbles_lastfm)
    FROM artists
)
```
|  | artist_lastfm | scrobbles_lastfm  |
| :---:   | :-: | :-: |
| 1 | The Beatles  | 517126254 |
  - Самый популярный тэг на ластфм
```
SELECT tag, COUNT(*) AS tag_freq
FROM artists
LATERAL VIEW explode(split(tags_lastfm, ';')) tags AS tag
WHERE tag != ''
GROUP BY tag
ORDER BY tag_freq DESC
LIMIT 1
```
|  | tag | tag_freq  |
| :---:   | :-: | :-: |
| 1 | seen live  | 81278 |
  - Самые популярные исполнители 10 самых популярных тегов ластфм
```
WITH top_tags AS (
    SELECT tag, COUNT(*) AS tag_freq
    FROM artists
    LATERAL VIEW EXPLODE(SPLIT(tags_lastfm, ';')) tags AS tag
    WHERE tag != ''
    GROUP BY tag
    ORDER BY tag_freq DESC
    LIMIT 10
    ),
artists_tags AS (
    SELECT artist_lastfm, tag, scrobbles_lastfm
    FROM artists
    LATERAL VIEW EXPLODE(SPLIT(tags_lastfm, ';')) tags AS tag
)
SELECT DISTINCT artist_lastfm, scrobbles_lastfm
FROM artists_tags
WHERE tag IN (
    SELECT tag 
    FROM top_tags
)
ORDER BY scrobbles_lastfm DESC
LIMIT 10
```
|  | artist_lastfm | scrobbles_number  |
| :---:   | :-: | :-: |
| 1 | The Beatles  | 517126254 |
| 2 | Radiohead | 499548797 |
| 3 |     Coldplay    | 360111850  |
| 4 |     Muse    | 344838631  |
| 5 |     Arctic Monkeys    | 332306552  |
| 6 |     Pink Floyd    | 313236119  |
| 7 |     Linkin Park    | 294986508  |
| 8 |     Red Hot Chili Peppers	    | 293784041  |
| 9 |     Lady Gaga    | 285469647  |
| 10 |     Metallica    | 281172228  |

  - Любой другой инсайт на ваше усмотрение: Первые 10 стран по числу скробблов 
```
SELECT TRIM(c) AS country, SUM(scrobbles_lastfm) AS scrobbles_number
FROM artists
LATERAL VIEW explode(split(country_lastfm, ';')) countries AS c
WHERE c != ''
GROUP BY TRIM(c)
ORDER BY scrobbles_number DESC
LIMIT 10
```
|  | country | scrobbles_number  |
| :---:   | :-: | :-: |
| 1 | United States  | 43881460988 |
| 2 | United Kingdom | 22616729714 |
| 3 |     Germany    | 6869790108  |
| 4 |     Japan    | 6709747806  |
| 5 |     France    | 6072174139  |
| 6 |     Sweden    | 5709705459  |
| 7 |     Canada    | 5512932513  |
| 8 |     Poland    | 4127177617  |
| 9 |     Russia    | 3179216862  |
| 10 |     Australia    | 2991917015  |
