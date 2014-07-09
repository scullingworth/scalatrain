package com.typesafe.training.scalatrain

import org.scalatest.{ Matchers, WordSpec }
import scala.collection.immutable.Seq

/**
 * Created by stevec on 2014-07-07.
 */
class TrainSpec extends WordSpec with Matchers {

  val vancouver = Station("Vancouver")
  val portland = Station("Portland")
  val seattle = Station("Seattle")
  val sanfrancisco = Station("San Francisco")

  val train1 = Train(InterCityExpress(1), Seq(Time(8, 0) -> vancouver, Time(11, 0) -> portland))
  val train2 = Train(RegionalExpress(2), Seq(Time(6, 0) -> seattle, Time(10, 0) -> portland))
  val train3 = Train(RegionalExpress(3), Seq(Time(6, 0) -> seattle, Time(10, 0) -> portland, Time(12, 0) -> sanfrancisco))

  "Creating Train" should {
    "throw an IllegalArgumentException for schedule with length < 2" in {
      an[IllegalArgumentException] should be thrownBy Train(RegionalExpress(1), Seq())
      an[IllegalArgumentException] should be thrownBy Train(RegionalExpress(1), Seq(Time() -> vancouver))
    }

    "successfully create a Train with a schedule of size 2" in {
      train1.schedule.length should equal(2)
    }

    "stations is initialized to station in schedule" in {
      train1.stations should contain(vancouver)
      train1.stations should contain(portland)
    }
  }

  "Train.backToBackStations" should {
    "build a seq of back-to-back stations" in {
      train3.backToBackStations should equal(Seq((seattle, portland), (portland, sanfrancisco)))
      train1.backToBackStations should equal(Seq((vancouver, portland)))
    }
  }
}
