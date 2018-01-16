package pl.mp.allegro.eudata

@SerialVersionUID(101L)
class CountryContracts(
    val isoCode: String,
    val contracts: Int = 0,
    val contractsValue: CurrencyMap = CurrencyMap.empty)
  extends Serializable {

  def this(isoCode: String, contractValues: CurrencyMap) = this(isoCode, 0, contractValues)

  def +(that: CountryContracts): CountryContracts =
    new CountryContracts(
      isoCode,
      contracts + that.contracts,
      contractsValue + that.contractsValue)


  override def toString: String = {
    val res = isoCode + ":\nTotal contracts: " + contracts.toString + "\nTotal cost:\n"

    contractsValue.foldLeft(res) {
      (acc, curr) => "%s\t%s %.2f\n".format(acc, curr._1, curr._2)
    } + "______________________________\n"
  }
}
