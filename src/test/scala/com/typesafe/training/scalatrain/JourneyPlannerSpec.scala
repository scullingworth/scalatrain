package com.typesafe.training.scalatrain

import org.scalatest.{ Matchers, WordSpec }
import scala.collection.immutable.Seq
import com.github.nscala_time.time.Imports._

/**
 * Created by stevec on 2014-07-07.
 */
class JourneyPlannerSpec extends WordSpec with Matchers {

  val vancouver = Station("Vancouver")
  val portland = Station("Portland")
  val seattle = Station("Seattle")
  val eugene = Station("Eugene")

  val train1 = Train(InterCityExpress(1), Seq(Time(8, 0) -> vancouver, Time(11, 0) -> portland))
  val train2 = Train(RegionalExpress(2), Seq(Time(6, 0) -> portland, Time(10, 0) -> seattle), Set(WeekDays.Mon))
  val train3 = Train(RegionalExpress(3), Seq(Time(6, 0) -> vancouver, Time(9, 0) -> seattle, Time(10, 0) -> portland), Set(WeekDays.Mon), Set(DateTime.now.withDate(2014, 7, 7)))
  val train4 = Train(RegionalExpress(4), Seq(Time(6, 0) -> vancouver, Time(8, 0) -> seattle, Time(10, 0) -> portland, Time(12, 0) -> eugene))
  val train5 = Train(RegionalExpress(5), Seq(Time(6, 0) -> vancouver, Time(8, 0) -> eugene))

  val train6 = Train(InterCityExpress(6), Seq(Time(8, 0) -> vancouver, Time(9, 0) -> seattle))
  val train7 = Train(InterCityExpress(7), Seq(Time(8, 0) -> seattle, Time(9, 0) -> portland))
  val train8 = Train(InterCityExpress(8), Seq(Time(9, 0) -> seattle, Time(10, 0) -> portland))
  val train9 = Train(InterCityExpress(9), Seq(Time(9, 0) -> seattle, Time(10, 0) -> vancouver))
  val train10 = Train(InterCityExpress(10), Seq(Time(10, 0) -> vancouver, Time(12, 0) -> portland))

  "Creating JourneyPlanner" should {
    "verify that stations is correctly initialized" in {
      val jp = new JourneyPlanner(Set(train1))
      jp.stations should contain(vancouver)
      jp.stations should contain(portland)
      jp.stations should have size 2
    }

    "verify that trainsAt returns a filtered list of trains by station" in {

    }

    "verify that stopsAt returns a list of stops" in {
      val jp = new JourneyPlanner(Set(train1, train2))
      jp.stopsAt(vancouver) should contain(Time(8, 0), train1)
    }
  }

  "JourneyPlanner.isShortTrip" should {
    "return true if with adjacent matching from/to stations" in {
      val jp = new JourneyPlanner(Set(train1))
      jp.isShortTrip(vancouver, portland) shouldBe true
    }

    "return true if train has 3 segments with adjacent matching from/to stations" in {
      val jp = new JourneyPlanner(Set(train3))
      jp.isShortTrip(seattle, portland) shouldBe true
    }

    "return true if train has 4 segments with adjacent matching from/to stations" in {
      val jp = new JourneyPlanner(Set(train4))
      jp.isShortTrip(seattle, portland) shouldBe true
    }

    "return true if train has matching from/to stations with another station in between" in {
      val jp = new JourneyPlanner(Set(train3))
      jp.isShortTrip(vancouver, portland) shouldBe true
    }

    "return false if train has matching From station only" in {
      val jp = new JourneyPlanner(Set(train2))
      jp.isShortTrip(portland, vancouver) shouldBe false
    }

    "return false if train has matching To/From station with two stations in between" in {
      val jp = new JourneyPlanner(Set(train4))
      jp.isShortTrip(vancouver, eugene) shouldBe false
    }

    "return false if train has matching To station only" in {
      val jp = new JourneyPlanner(Set(train2))
      jp.isShortTrip(vancouver, seattle) shouldBe false
    }

    "return false if train has matching To/From stations in wrong order" in {
      val jp = new JourneyPlanner(Set(train3))
      jp.isShortTrip(seattle, vancouver) shouldBe false
    }
  }

  "JourneyPlanner.departingStationToHopsMap" should {
    "return a map of 3 vancouver " in {
      val jp = new JourneyPlanner(Set(train1, train3, train4))
      jp.departingStationToHopsMap.get(vancouver).get should contain(Hop(vancouver, portland, train1))
      jp.departingStationToHopsMap.get(vancouver).get should contain(Hop(vancouver, seattle, train3))
      jp.departingStationToHopsMap.get(vancouver).get should contain(Hop(vancouver, seattle, train4))
    }
  }

  "JourneyPlanner.pathsBetweenTwoStations" should {
    "return a single hop for a simple schedule" in {
      val jp = new JourneyPlanner(Set(train1))
      jp.pathsBetweenTwoStations(vancouver, portland, Time(8)).size should equal(1)
      jp.pathsBetweenTwoStations(vancouver, portland, Time(8)).head.size should equal(1)
      jp.pathsBetweenTwoStations(vancouver, portland, Time(8)).head.head.from should equal(vancouver)
      jp.pathsBetweenTwoStations(vancouver, portland, Time(8)).head.head.to should equal(portland)
    }
  }

  "JourneyPlanner.pathsBetweenTwoStations" should {
    "return a two hops for a two simple schedules" in {
      val jp = new JourneyPlanner(Set(train1, train3, train5))
      jp.pathsBetweenTwoStations(vancouver, portland, Time(6)).size should equal(2)
    }
  }

  "JourneyPlanner.pathsBetweenTwoStations" should {
    "return nothing if departure time is too late" in {
      val jp = new JourneyPlanner(Set(train1))
      jp.pathsBetweenTwoStations(vancouver, portland, Time(9)).size should equal(0)
    }

    "return nothing if connection is impossible" in {
      val jp = new JourneyPlanner(Set(train6, train7))
      jp.pathsBetweenTwoStations(vancouver, portland, Time(8)).size should equal(0)
    }

    "return one path if connection is possible" in {
      val jp = new JourneyPlanner(Set(train6, train7, train8))
      jp.pathsBetweenTwoStations(vancouver, portland, Time(8)).size should equal(1)
    }

    "return 2 paths, ignoring cycle" in {
      val jp = new JourneyPlanner(Set(train6, train8, train9, train10))
      jp.pathsBetweenTwoStations(vancouver, portland, Time(8)).size should equal(2)
    }
  }

  "JourneyPlanner.sortByTotalTime" should {
    "sum of duration of each hop" in {
      val jp = new JourneyPlanner(Set(train4))
      JourneyPlanner.totalTime(jp.pathsBetweenTwoStations(vancouver, eugene, Time(6)).head) should equal(6 * 60)
    }

    "sort paths by duration in ascending order" in {
      val path1 = Seq(Hop(vancouver, portland, train1)) // 3 hours
      val path2 = Seq(Hop(vancouver, portland, train3)) // 4 hours
      val path3 = Seq(Hop(vancouver, eugene, train4)) // 6 hours
      val path4 = Seq(Hop(vancouver, seattle, train6)) // 1 hour
      JourneyPlanner.sortByTotalTime(Set(path1, path2, path3, path4)) should equal(Seq(path4, path1, path2, path3))
    }

    "sort paths by cost in ascending order" in {
      val path1 = Seq(Hop(vancouver, portland, train1, 300))
      val path2 = Seq(Hop(vancouver, portland, train3, 150))
      val path3 = Seq(Hop(vancouver, seattle, train4, 200), Hop(seattle, portland, train4, 200))
      val path4 = Seq(Hop(vancouver, seattle, train6, 250))
      JourneyPlanner.sortByTotalCost(Set(path1, path2, path3, path4)) should equal(Seq(path2, path4, path1, path3))
    }
  }
  
  "JourneyPlanner.trainsRunningOn" should {
    
    "all trains run on Monday July 14th 2014" in {
      val jp = new JourneyPlanner(Set(train1, train2, train3))
      val trainsRunning = jp.trainsRunningOn(DateTime.now.withDate(2014, 7, 14))
      trainsRunning should contain (train1)
      trainsRunning should contain (train2)
      trainsRunning should contain (train3)
      trainsRunning.size should be (3)
    }
    
    
    "2 trains run on Monday July 7th 2014" in {
      val jp = new JourneyPlanner(Set(train1, train2, train3))
      val trainsRunning = jp.trainsRunningOn(DateTime.now.withDate(2014, 7, 7))
      trainsRunning should contain (train1)
      trainsRunning should contain (train2)
      trainsRunning.size should be (2)
    }
    
    "1 train runs on Tuesdays" in {
      val jp = new JourneyPlanner(Set(train1, train2, train3))
      val trainsRunning = jp.trainsRunningOn(DateTime.now.withDate(2014, 7, 8))
      trainsRunning should contain (train1)
      trainsRunning.size should be (1)
    }
  }
}