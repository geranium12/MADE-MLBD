package main

import org.slf4j.LoggerFactory

object Main {
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(getClass.getSimpleName)
    try {
      logger.info("Getting args...")
      val trainPath = args(0)
      val testPath = args(1)

      logger.info("Reading train and test data...")
      val dataReaderWriter = new DataReaderWriter
      val train = dataReaderWriter.readData(trainPath)
      val xTest = dataReaderWriter.readData(testPath)

      val xTrain = train(::, 0 until train.cols - 1).toDenseMatrix
      val yTrain = train(::, -1).toDenseVector

      logger.info("Scaling data...")
      val scaler = new MinMaxScaler
      scaler.fit(xTrain, yTrain)
      val (transformedXTrain, transformedYTrain) = scaler.transform(xTrain, yTrain)

      logger.info("Fitting Linear Regression model...")
      val model = new LinearRegression(nIters = 200)
      model.fit(transformedXTrain, transformedYTrain)

      logger.info("Predicting test results...")
      val (transformedXTest, _) = scaler.transform(xTest)
      val (_, predictedYTest) = scaler.inverseTransform(transformedXTrain, model.predict(transformedXTest))

      logger.info("Writing test results...")
      dataReaderWriter.writeData("results-" + java.time.LocalDate.now + ".csv", predictedYTest.toDenseMatrix.t)
    } catch {
      case _: NumberFormatException =>
        logger.error("NumberFormatException: the input data has non-numerical columns!")
      case _: ArrayIndexOutOfBoundsException =>
        logger.error("ArrayIndexOutOfBoundsException: you did not specify either input train data or input test data!")
    }
  }
}
