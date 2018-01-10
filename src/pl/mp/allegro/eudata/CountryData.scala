package pl.mp.allegro.eudata

import pl.mp.allegro.eudata.CurrencyMap.CurrencyMap
import scalaz._
import Scalaz._
import scala.collection.immutable

object CurrencyMap {
  type CurrencyMap = immutable.Map[String, Long]
}

class CountryData(val isoCode: String,
                  var contracts: Int = 0,
                  var contractsValue: CurrencyMap = new immutable.HashMap()) {

  def +(that: CountryData): CountryData = {
    if (isoCode != that.isoCode) {
      new CountryData(isoCode, contracts, contractsValue)
    } else {
      new CountryData(isoCode,
                      contracts + that.contracts,
                      contractsValue |+| that.contractsValue)
    }
  }

  override def toString: String = {
    isoCode + "\n" + contracts.toString + "\n" + contractsValue.toString + "\n"
  }
}
