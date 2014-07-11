package com.typesafe.training.scalatrain

import org.scalatest.{ Matchers, WordSpec }
import scala.collection.immutable.Seq
import com.github.nscala_time.time.Imports._
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
  
  
  
  val exceptionDate = DateTime.now.withDate(2014, 7, 7)
  val train4 = Train(InterCityExpress(4), Seq(Time(8, 0) -> vancouver, Time(11, 0) -> portland), Set(WeekDays.Mon), Set(exceptionDate))

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

  "Train.departureTimes" should {
    "get a sequence of departure times for all stations" in {
      train1.departureTimes should equal(Seq((vancouver, Time(8, 0)), (portland, Time(11, 0))))
    }
  }

  "Hop" should {
    "arrival and departure time should be correct for stations" in {
      Hop(vancouver, portland, train1).departureTime should equal(Time(8, 0))
      Hop(vancouver, portland, train1).arrivalTime should equal(Time(11, 0))
    }
    "throw and IllegalArgumentException if 'to' is before the 'from'" in {
      an[IllegalArgumentException] should be thrownBy Hop(portland, vancouver, train1)
    }
  }
  
  "Train.isRunningOn" should {
    
    "should run on a default train with no exception dates" in {
      train1.isRunningOn(DateTime.now) should equal (true)
    }
    
    "should not run on Tuesdays for a train running only on Mondays with no exception" in {
      train4.isRunningOn(DateTime.now.withDate(2014, 7, 8)) should equal (false) // Tuesday
    }
    
    "should run on Monday for a train running only on Mondays with no exception" in {
     train4.isRunningOn(DateTime.now.withDate(2014, 7, 14)) should equal (true) // Monday, no exception, okay
    }
    
    "should not run on Monday July 7th because of exception" in {
      train4.isRunningOn(DateTime.now.withDate(2014, 7, 7)) should equal (false) // Tuesday
    }
  }
}
