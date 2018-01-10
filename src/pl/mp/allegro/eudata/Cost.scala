package pl.mp.allegro.eudata

class Cost(val currency: String, val value: Long) {
  def this(currency: String, strValue: String) = this(currency, CostUtils.stringToLong(strValue))

  def isEmpty: Boolean = {
    currency.isEmpty || value == 0
  }
}

private object CostUtils {
  def stringToLong(str: String): Long = {
    (if (!str.contains(".")) {
      str + ".00"
    } else {
      str
    }) replaceAll("\\.", "") toLong
  }
}
