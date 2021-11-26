package main

import breeze.linalg.{DenseMatrix, DenseVector, max, min}

class MinMaxScaler {
  private var _minXTrain: Array[Double] = Array()
  private var _maxXTrain: Array[Double] = Array()
  private var _minYTrain: Double = 0
  private var _maxYTrain: Double = 0

  private def vectorsSeqToDenseMatrix(seq: IndexedSeq[DenseVector[Double]]): DenseMatrix[Double] = {
    var denseMatrix = seq(0).toDenseMatrix.t
    for (i <- 1 until seq.length) {
      denseMatrix = DenseMatrix.horzcat(denseMatrix, seq(i).toDenseMatrix.t)
    }
    denseMatrix
  }

  def min_x_train: Array[Double] = _minXTrain

  def max_x_train: Array[Double] = _maxXTrain

  def min_y_train: Double = _minYTrain

  def max_y_train: Double = _maxYTrain

  def fit(x: DenseMatrix[Double], y: DenseVector[Double]): Unit = {
    _minXTrain = new Array[Double](x.cols)
    _maxXTrain = new Array[Double](x.cols)

    for (j <- 0 until x.cols) {
      min_x_train(j) = min(x(::, j))
      max_x_train(j) = max(x(::, j))
    }
    _minYTrain = min(y)
    _maxYTrain = max(y)
  }

  def transform(x: DenseMatrix[Double], y: DenseVector[Double] = DenseVector[Double]()): (DenseMatrix[Double], DenseVector[Double]) = {
    val transformed_x_train_seq = (0 until x.cols).map { j =>
      x(::, j).map(el => el - _minXTrain(j)) / (_maxXTrain(j) - _minXTrain(j))
    }
    val transformed_x_train = vectorsSeqToDenseMatrix(transformed_x_train_seq)
    if (y.length != 0) {
      val transformed_y_train = y.map(el => (el - _minYTrain) / (_maxYTrain - _minYTrain))
      return (transformed_x_train, transformed_y_train)
    }
    (transformed_x_train, DenseVector[Double]())
  }

  def inverseTransform(x: DenseMatrix[Double], y: DenseVector[Double] = DenseVector[Double]()): (DenseMatrix[Double], DenseVector[Double]) = {
    val transformed_x_train_seq = (0 until x.cols).map { j =>
      x(::, j).map(el => el * (_maxXTrain(j) - _minXTrain(j)) + _minXTrain(j))
    }
    val transformed_x_train = vectorsSeqToDenseMatrix(transformed_x_train_seq)
    if (y.length != 0) {
      val transformed_y_train = y.map(el => (el * (_maxYTrain - _minYTrain) + _minYTrain))
      return (transformed_x_train, transformed_y_train)
    }
    (transformed_x_train, DenseVector[Double]())
  }

}
