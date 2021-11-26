## TF-IDF с использованием Spark DataFrame API

### Условие
По данным "Trip advisor hotel reviews" (https://www.kaggle.com/andrewmvd/trip-advisor-hotel-reviews) посчитать TF-IDF (https://ru.wikipedia.org/wiki/TF-IDF) с помощью Spark DataFrame / Dataset API без использования Spark ML
Этапы
- Привести все к одному регистру
- Удалить все спецсимволы
- Посчитать частоту слова в предложении
- Посчитать количество документов со словом
- Взять только 100 самых встречаемых
- Сджойнить две полученные таблички и посчитать Tf-Idf (только для слов из предыдущего пункта)
- Запайвотить табличку

### Решение

```
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import scala.math._
import scala.math.{log=>mathLog}

val spark = SparkSession.builder()
  .master("local[*]")
  .appName("homework-04")
  .getOrCreate()
  
val df = spark.read
  .option("header", "true")
  .option("inferSchema", "true")
  .csv("/srv/data/tripadvisor_hotel_reviews.csv")
  
df.show

val normalisedDf = df
    .withColumn("Review", lower(col("Review")))
    .withColumn("Review", regexp_replace(col("Review"), "[^0-9a-zA-Z ]", ""))
    
normalisedDf.show

val wordDf = normalisedDf
    .select(col("Review"))
    .withColumn("Review", split(trim(col("Review")), " "))
    .withColumn("ID", monotonically_increasing_id())
    .withColumn("Word", explode(col("Review")))
    
wordDf.show

val tf = wordDf
    .withColumn("Frequency", count(col("Review")).over(Window.partitionBy(col("ID"), col("Word"))))
    .distinct()
    .withColumn("TF", col("Frequency") / size(col("Review")))
    .select("Word", "ID", "TF")
    
tf.show

val numberOfDocs = df.count()

def getIdfHelper(freq: Int): Double = {
    mathLog((numberOfDocs + 1) / (freq + 1))
}

val getIdf = spark.udf.register("getIdf", getIdfHelper _)

val idf = wordDf
    .groupBy("Word")
    .agg(countDistinct("ID").as("DocumentFrequency"))
    .withColumn("DocumentFrequency", col("DocumentFrequency"))
    .orderBy(desc("DocumentFrequency"))
    .withColumn("DocumentsNumber", lit(numberOfDocs))
    .withColumn("IDF", getIdf(col("DocumentFrequency")).cast("Double"))
    .select("Word", "IDF")
    
idf.show

val tfIdf = tf
    .join(idf, Seq("Word"), joinType="right")
    .na.drop()
    .withColumn("TFIDF", col("TF") * col("IDF"))
    .select("Word", "ID", "TFIDF")
    .orderBy(desc("TFIDF"))
    .limit(100)
    
tfIdf.show

val tfIdfPivoted = tfIdf
  .groupBy("ID")
  .pivot("Word")
  .agg(first(col("TFIDF")))
  .na.fill(0.0)
  
tfIdfPivoted.show(5, 50, true)
```

### Результаты
```df.show```
|              Review|Rating|
| :---:   | :-: |
|nice hotel expens...|     4|
|ok nothing specia...|     2|
|nice rooms not 4*...|     3|
|unique, great sta...|     5|
|great stay great ...|     5|
|love monaco staff...|     5|
|cozy stay rainy c...|     5|
|excellent staff, ...|     4|
|hotel stayed hote...|     5|
|excellent stayed ...|     5|
|poor value stayed...|     2|
|nice value seattl...|     4|
|nice hotel good l...|     4|
|nice hotel not ni...|     3|
|great hotel night...|     4|
|horrible customer...|     1|
|disappointed say ...|     2|
|fantastic stay mo...|     5|
|good choice hotel...|     5|
|hmmmmm say really...|     3|

```normalisedDf.show```
|              Review|Rating|
| :---:   | :-: |
|nice hotel expens...|     4|
|ok nothing specia...|     2|
|nice rooms not 4 ...|     3|
|unique great stay...|     5|
|great stay great ...|     5|
|love monaco staff...|     5|
|cozy stay rainy c...|     5|
|excellent staff h...|     4|
|hotel stayed hote...|     5|
|excellent stayed ...|     5|
|poor value stayed...|     2|
|nice value seattl...|     4|
|nice hotel good l...|     4|
|nice hotel not ni...|     3|
|great hotel night...|     4|
|horrible customer...|     1|
|disappointed say ...|     2|
|fantastic stay mo...|     5|
|good choice hotel...|     5|
|hmmmmm say really...|     3|

```wordDf.show```
|              Review| ID|       Word|
| :---:   | :-: | :-: |
|[nice, hotel, exp...|  0|       nice|
|[nice, hotel, exp...|  0|      hotel|
|[nice, hotel, exp...|  0|  expensive|
|[nice, hotel, exp...|  0|    parking|
|[nice, hotel, exp...|  0|        got|
|[nice, hotel, exp...|  0|       good|
|[nice, hotel, exp...|  0|       deal|
|[nice, hotel, exp...|  0|       stay|
|[nice, hotel, exp...|  0|      hotel|
|[nice, hotel, exp...|  0|anniversary|
|[nice, hotel, exp...|  0|    arrived|
|[nice, hotel, exp...|  0|       late|
|[nice, hotel, exp...|  0|    evening|
|[nice, hotel, exp...|  0|       took|
|[nice, hotel, exp...|  0|     advice|
|[nice, hotel, exp...|  0|   previous|
|[nice, hotel, exp...|  0|    reviews|
|[nice, hotel, exp...|  0|        did|
|[nice, hotel, exp...|  0|      valet|
|[nice, hotel, exp...|  0|    parking|

```tf.show```
|      Word| ID|                  TF|
| :---:   | :-: | :-: |
|      room|  0|0.034482758620689655|
|    better|  1|               0.008|
|attractive|  6|0.009900990099009901|
|  positive|  6|0.009900990099009901|
| concierge|  7|0.023529411764705882|
|        nt| 10|  0.0425531914893617|
|     clean| 12|0.011904761904761904|
|   concert| 12|0.011904761904761904|
|      stay| 15|0.009345794392523364|
|      desk| 16|0.024793388429752067|
|       bed| 19| 0.00641025641025641|
| excellent| 30| 0.02702702702702703|
|    really| 32|0.011627906976744186|
| cringeshe| 44| 0.02702702702702703|
|      mind| 46|0.014285714285714285|
|    pretty| 51|0.011904761904761904|
|     steer| 52|            0.015625|
|     tacky| 54|0.007633587786259542|
|   staying| 58|0.027777777777777776|
|       etc| 63| 0.01020408163265306|

```idf.show```
|     Word|               IDF|
| :---:   | :-: |
|    hotel|               0.0|
|     room|               0.0|
|      not|               0.0|
|    staff|               0.0|
|    great|               0.0|
|     stay|0.6931471805599453|
|     good|0.6931471805599453|
|   stayed|0.6931471805599453|
|       nt|0.6931471805599453|
|    rooms|0.6931471805599453|
| location|0.6931471805599453|
|     just|0.6931471805599453|
|    clean|0.6931471805599453|
|     nice|0.6931471805599453|
|      did|0.6931471805599453|
|breakfast|0.6931471805599453|
|       no|1.0986122886681098|
|    night|1.0986122886681098|
|  service|1.0986122886681098|
|     time|1.0986122886681098|

```tfIdf.show```
|        Word|         ID|             TFIDF|
| :---:   | :-: | :-: |
|      platos|       2473| 1.539107110749858|
|      hostal|17179871819|1.3965725502937885|
|       creak|       1881|1.3134085782313967|
|commonwealth|17179869733|1.3133810687284309|
|     vehicle|        480|1.1209850644057129|
|    domenico| 8589939550|1.1081571197398978|
|     avenida|       5642| 1.075629493976716|
|      adagio|17179871139|1.0667095742181367|
|     miramar|17179873569|0.9717659201360166|
|         h10|17179874643|0.9600386167963231|
|     aviatic|       5096|0.9512912831757001|
|    regencia| 8589939873|0.9452007007635617|
|      parrot|       2687|0.9370878910704631|
|    draycott|       4271|0.9303115903493051|
|       metti| 8589934712|0.9234642664499149|
|   animation|25769804599|0.9204718241867212|
|   residence|       5186| 0.907789561944385|
|        mice|25769804438|0.9024552550163419|
| wagenstraat| 8589938338|0.8936750965644337|
|         pas|17179874598|0.8829079952564837|
