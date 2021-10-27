package main

import breeze.linalg.{DenseMatrix, DenseVector, csvread, csvwrite}

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class DataReaderWriter {

  def readData(fileName: String): DenseMatrix[Double] = {
    val data = csvread(file = new File(fileName), separator = ',', skipLines = 1)
    data
  }

  def writeData(fileName: String, data: DenseMatrix[Double]): Unit = {
    csvwrite(file = new File("log/" + fileName), mat = data, separator = ',')
  }

}
