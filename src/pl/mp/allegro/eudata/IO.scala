package pl.mp.allegro.eudata

import java.io.{File, PrintWriter}

object OutputHandler {
  def exportToCsv(countries: Iterator[CountryContracts], filename: String): Unit = {
    val pw = new PrintWriter(new File(filename + ".txt"))

    countries.map(countryDataToCsv).foreach(pw.write)
    pw.close()
  }

  def countryDataToCsv(data: CountryContracts): String = {
    var res = data.isoCode + ":\nTotal contracts: " + data.contracts.toString + "\nTotal cost:\n"

    data.contractsValue.foldLeft(res) {
      (acc, curr) => "%s\t%s %.2f\n".format(acc, curr._1, curr._2 / 100.0)
    } + "______________________________\n"
  }
}

object InputHandler {
  def listFilesInDir(dirname: String, extensons: List[String] = List()): Option[Iterator[File]] = {
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
      }) toIterator)
    } else {
      Some(dir.listFiles toIterator)
    }
  }
}
