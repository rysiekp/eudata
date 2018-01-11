package pl.mp.allegro.eudata

class Cost(val currency: String, val value: Long) {
  def this(currency: String, strValue: String) = this(currency, CostValueParser.parse(strValue))

  def isEmpty: Boolean = currency.isEmpty || value == 0
}

private object CostValueParser {
  def parse(str: String): Long = {
    (if (!str.contains(".")) {
      str + ".00"
    } else {
      str
    }) replaceAll("\\.", "") toLong
  }
}
