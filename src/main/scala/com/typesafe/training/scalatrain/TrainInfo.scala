package com.typesafe.training.scalatrain

/**
 * Created by stevec on 2014-07-08.
 */
sealed abstract class TrainInfo {
  def number: Int
}

case class InterCityExpress(number: Int, hasWifi: Boolean = false) extends TrainInfo {
}

case class RegionalExpress(number: Int) extends TrainInfo {
}

case class BavarianRegional(number: Int) extends TrainInfo {
}
