package pl.mp.allegro.eudata

import scala.collection.immutable.{HashMap, Map}

@SerialVersionUID(102L)
class CurrencyMap(val map: HashMap[String, Double]) extends Serializable {

  def this(map: Map[String, Double]) =  this({
    var hashMap = HashMap.empty[String, Double]
    map.foreach {
      case (k, v) => hashMap = hashMap + (k -> v)
    }
    hashMap
  })

  def +(that: CurrencyMap): CurrencyMap = {
    val merged = map ++ that.map.toSeq
    val grouped = merged.groupBy(_._1).mapValues(_.values.sum)
    new CurrencyMap(grouped)
  }

  def foldLeft[A](acc: A)(op: (A, (String, Double)) => A): A = map.foldLeft(acc)(op)
  def keys: Iterable[String] = map.keys
  def getOrZero(key: String): Double = map.getOrElse(key, 0.0)
}

object CurrencyMap {
  def empty = new CurrencyMap(HashMap.empty[String, Double])
}
