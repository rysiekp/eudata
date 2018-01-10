package pl.mp.allegro.eudata

import java.io.File
import scala.xml.{NodeSeq, XML}
import scala.annotation.tailrec

object EUDataCollector {
  def main(args: Array[String]): Unit = {

    if (args.length < 1) {
      System.err.println("Data directory not provided")
      System.exit(1)
    }

    val (data_dir, output_filename) = if (args.length == 1) {
      (args(0), "out")
    } else {
      (args(0), args(1))
    }

    val dirs = InputHandler.listFilesInDir(data_dir).getOrElse(Stream.empty)

    val files = dirs.flatMap({
      dir => InputHandler.listFilesInDir(dir.getAbsolutePath, List("xml")).getOrElse(Stream.empty)
    })

    val stats = files.flatMap(getContract).groupBy(_.isoCode).map {
      case (k, v) => (k, sumCountryDataList(v, k))
    }

    val currencies = stats.values.flatMap(_.contractsValue.keys).toSet.toList

    OutputHandler.exportToCsv(currencies, stats.values.toList, output_filename)
  }


  def getContract(file: File): Option[CountryData] = {
    val xml = XML.loadFile(file)

    val noticeCode = xml \\ "CODED_DATA_SECTION" \\ "CODIF_DATA" \\ "TD_DOCUMENT_TYPE" \ "@CODE" text
    val countryCode = xml \\ "CODED_DATA_SECTION" \\ "NOTICE_DATA" \\ "ISO_COUNTRY" \ "@VALUE" text

    noticeCode match {
      case "3" => Some(new CountryData(countryCode, 1))
      case "7" =>
        val awardedContracts = xml \\ "AWARD_CONTRACT"
        // group by currency, leave only second part of tuple, sum the list
        val contractsValues = awardedContracts.flatMap(getAwardedContractValues).
          groupBy(_.currency).
          mapValues(_.map(_.value).sum)
        Some(new CountryData(countryCode, 0, contractsValues))
      case _ => None
    }
  }

  def getAwardedContractValues(xml: NodeSeq): Option[Cost] = {
    val currency = xml \\ "VAL_TOTAL" \\ "@CURRENCY" text
    var value = xml \\ "VAL_TOTAL" text

    val cost = new Cost(currency, value)

    if (cost.isEmpty) {
      None
    } else {
      Some(cost)
    }
  }

  def sumCountryDataList(l: Stream[CountryData], countryCode: String): CountryData = {
    @tailrec
    def inner(l: Stream[CountryData], acc: CountryData): CountryData = {
      l match {
        case h #:: t => inner(t, acc + h)
        case _ => acc
      }
    }
    inner(l, new CountryData(countryCode))
  }
}
