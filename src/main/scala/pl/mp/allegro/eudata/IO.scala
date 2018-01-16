package pl.mp.allegro.eudata

import java.io.{File, PrintWriter}

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object IO {
  def readFiles(dataDir: String, sc: SparkContext): RDD[String] = {
    sc.wholeTextFiles(dataDir + "/*/*.xml").map(_._2)
  }

  def exportToFile(countries: Iterable[CountryContracts],
                   filename: String,
                   ext: String = ".csv",
                   sep: String = ","): Unit = {
    val pw = new PrintWriter(new File(filename + ext))

    val currencies = countries.flatMap(_.contractsValue.keys).toSet
    pw.write(csvHeader(currencies))

    countries.map(countryData(_, currencies)).foreach(pw.write)

    pw.close()
  }

  private def csvHeader(currencies: Iterable[String], sep: String = ","): String =
    currencies.foldLeft("ISO_CODE" + sep + "CONTRACTS") {
      (acc, curr) => acc + sep + curr
    } + "\n"

  private def countryData(country: CountryContracts, currencies: Iterable[String], sep: String = ","): String =
    currencies.foldLeft(country.isoCode + sep + country.contracts) {
      (acc, curr) => acc + sep + "%.2f".format(country.contractsValue.getOrZero(curr))
    } + "\n"
}