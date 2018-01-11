package pl.mp.allegro.eudata

import java.io.File
import scala.xml.{NodeSeq, XML}

object EUDataCollector {
  val ContractNoticeCode = "3"
  val ContractAwardsCode = "7"


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

    val files = dirs.flatMap {
      dir => InputHandler.listFilesInDir(dir.getAbsolutePath, List("xml")).getOrElse(Stream.empty)
    }

    val stats = files.flatMap(getContract).toStream.groupBy(_.isoCode).map {
      case (k, v) => (k, v.fold(new CountryContracts(k))(_ + _))
    } valuesIterator

    OutputHandler.exportToCsv(stats, output_filename)
  }


  def getContract(file: File): Option[CountryContracts] = {
    val xml = XML.loadFile(file)

    val noticeCode = xml \\ "CODED_DATA_SECTION" \\ "CODIF_DATA" \\ "TD_DOCUMENT_TYPE" \ "@CODE" text
    val countryCode = xml \\ "CODED_DATA_SECTION" \\ "NOTICE_DATA" \\ "ISO_COUNTRY" \ "@VALUE" text

    noticeCode match {
      case ContractNoticeCode => Some(new CountryContracts(countryCode, contracts = 1))
      case ContractAwardsCode =>
        val awardedContracts = xml \\ "AWARD_CONTRACT"
        val contractsValues = awardedContracts.flatMap(getAwardedContractValues)
            .groupBy(_.currency)
            .mapValues(_.map(_.value).sum)
        Some(new CountryContracts(countryCode, contracts = 0, contractsValues))
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
}
