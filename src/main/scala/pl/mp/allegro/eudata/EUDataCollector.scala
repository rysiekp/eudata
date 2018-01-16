package pl.mp.allegro.eudata

import scala.xml.{NodeSeq, XML}
import org.apache.spark.{SparkConf, SparkContext}

object EUDataCollector {
  val ContractNoticeCode = "3"
  val ContractAwardsCode = "7"

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("eudata").setMaster("local[*]"))

    if (args.length < 1) {
      System.err.println("Data directory not provided")
      System.exit(1)
    }
    val (dataDir, outputFilename) = if (args.length == 1)
      (args(0), "out")
    else
      (args(0), args(1))

    val files = IO.readFiles(dataDir, sc)

    val stats =
      files.flatMap(getContract)
      .keyBy(_.isoCode)
      .reduceByKey(_ + _)
      .values
      .collect

    IO.exportToFile(stats, outputFilename)
  }


  def getContract(file: String): Option[CountryContracts] = {
    val xml = XML.loadString(file)

    val noticeCode = (xml \\ "CODED_DATA_SECTION" \\ "CODIF_DATA" \\ "TD_DOCUMENT_TYPE" \ "@CODE").text
    val countryCode = (xml \\ "CODED_DATA_SECTION" \\ "NOTICE_DATA" \\ "ISO_COUNTRY" \ "@VALUE").text

    noticeCode match {
      case ContractNoticeCode => Some(new CountryContracts(countryCode, contracts = 1))
      case ContractAwardsCode =>
        val awardedContracts = xml \\ "AWARD_CONTRACT"
        val contractValues = new CurrencyMap(awardedContracts.flatMap(getAwardedContractValues)
                                                             .groupBy(_._1)
                                                             .mapValues(_.map(_._2).sum))
        Some(new CountryContracts(countryCode, contractValues))
      case _ => None
    }
  }

  def getAwardedContractValues(xml: NodeSeq): Option[(String, Double)] = {
    val currency = (xml \\ "VAL_TOTAL" \\ "@CURRENCY").text
    val value = (xml \\ "VAL_TOTAL").text

    if (currency.nonEmpty && value.nonEmpty)
      Some(currency, value.toDouble)
    else
      None
  }
}
