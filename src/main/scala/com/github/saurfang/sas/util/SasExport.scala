package com.github.saurfang.sas.util

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.LogManager

/**
 * Export sas7bdat file to parquet/csv.
 * First argument is the input file. Second argument is the output path.
 * Output type is determined by the extension (.csv or .parquet)
 */
object SasExport {

  def main(args: Array[String]): Unit = {
    val log = LogManager.getRootLogger
    log.info(args.mkString(" "))   

    val sparkConf = new SparkConf()
      .setAppName("SAStoCSV")
    if (!sparkConf.contains("spark.master")) {
      sparkConf.setMaster("local")
    }
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)

    import com.github.saurfang.sas.spark._
    val df = sqlContext.sasFile(args(0))

    val output = args(1)
    if (output.endsWith(".csv")) {
      import com.databricks.spark.csv._
      df.saveAsCsvFile(output, Map("header" -> "true"))
    } else if (output.endsWith(".parquet")) {
      df.saveAsParquetFile(output)
    }
  }
}
