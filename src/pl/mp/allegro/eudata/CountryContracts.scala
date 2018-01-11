package pl.mp.allegro.eudata

import pl.mp.allegro.eudata.CurrencyMap.CurrencyMap

import scalaz.Scalaz._
import scala.collection.immutable

object CurrencyMap {
  type CurrencyMap = immutable.Map[String, Long]
}

class CountryContracts(val isoCode: String,
                       var contracts: Int = 0,
                       var contractsValue: CurrencyMap = new immutable.HashMap()) {

  def +(that: CountryContracts): CountryContracts = {
    new CountryContracts(isoCode,
      contracts + that.contracts,
      contractsValue |+| that.contractsValue)
  }
}
