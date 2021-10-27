package main

import breeze.linalg.{DenseMatrix, DenseVector, sum}

class LinearRegression(lr: Double = 1e-3, nIters: Int = 1000, trainSize: Double = 0.8) {
  private var _weights = DenseVector[Double]()
  private var _intercept: Double = 0

  def weights: DenseVector[Double] = _weights

  def intercept: Double = _intercept

  def getError(y: DenseVector[Double], predictedY: DenseVector[Double]): Double = {
    sum((y - predictedY).map(el => el * el)) / y.length
  }

  def fit(x: DenseMatrix[Double], y: DenseVector[Double]) {
    val trainNumber = (trainSize * x.rows).asInstanceOf[Int]
    val xTrain = x(0 until trainNumber, ::).toDenseMatrix
    val xVal = x(trainNumber until x.rows, ::).toDenseMatrix
    val yTrain = y(0 until trainNumber).toDenseVector
    val yVal = y(trainNumber until y.length).toDenseVector

    val trainCols = xTrain.cols
    _weights = DenseVector.zeros[Double](trainCols)
    val trainErrors = DenseVector.zeros[Double](nIters)
    val valErrors = DenseVector.zeros[Double](nIters)
    for (i <- 0 until nIters) {
      val predictedYVal = (xVal * weights.toDenseMatrix.t).toDenseVector
      valErrors(i) = getError(yVal, predictedYVal)
      val predictedYTrain = (xTrain * weights.toDenseMatrix.t).toDenseVector
      trainErrors(i) = getError(yTrain, predictedYTrain)
      val dw = ((predictedYTrain - yTrain).toDenseMatrix * xTrain).map(el => el * 2.0 / trainCols).toDenseVector
      val db = 2.0 / trainCols * sum((predictedYTrain - yTrain))
      _weights = _weights - dw * lr
      _intercept = _intercept - db * lr
    }
    val dataReaderWriter = new DataReaderWriter
    dataReaderWriter.writeData("train-errors-" + java.time.LocalDate.now + ".csv", trainErrors.toDenseMatrix.t)
    dataReaderWriter.writeData("validation-errors-" + java.time.LocalDate.now + ".csv", valErrors.toDenseMatrix.t)
  }

  def predict(x: DenseMatrix[Double]): DenseVector[Double] = {
    (x * weights.toDenseMatrix.t).toDenseVector
  }
}