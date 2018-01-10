package pl.mp.allegro.eudata

import java.io.{BufferedWriter, File, FileWriter}

object OutputHandler {
  def exportToCsv(currencies: List[String], countries: List[CountryData], filename: String): Unit = {
    val out = new File(filename + "csv")
    val bw = new BufferedWriter(new FileWriter(out))

    // csv header
    bw.write("COUNTRY_ISO_CODE,CONTRACTS")
    currencies.foreach {
      curr => bw.write("," + curr)
    }
    bw.write("\n")

    countries.map(countryDataToCsv(_, currencies)).foreach(bw.write)
    bw.close()
  }

  def countryDataToCsv(data: CountryData, currencies: List[String], sep: Char = ','): String = {
    var res = data.isoCode + "," + data.contracts.toString
    currencies.foldLeft(res) {
      // for each currency we print it's value with 2 decimal places
      (acc, curr) => "%s,%.2f".format(acc, data.contractsValue.getOrElse(curr, 0L) / 100.0)
    } + "\n"
  }
}

object InputHandler {
  def listFilesInDir(dirname: String, extensons: List[String] = List()): Option[Stream[File]] = {
    val dir = new File(dirname)

    if (!dir.exists) {
      System.err.println("%s doesn't exist".format(dirname))
      return None
    }

    if (!dir.isDirectory) {
      System.err.println("%s is not a directory".format(dirname))
      return None
    }

    if (extensons.nonEmpty) {
      Some(dir.listFiles(_.isFile).filter({
        file => extensons.exists(file.getName.endsWith(_))
      }) toStream)
    } else {
      Some(dir.listFiles.toStream)
    }
  }
}
