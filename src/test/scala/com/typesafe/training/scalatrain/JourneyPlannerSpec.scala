package com.typesafe.training.scalatrain

import com.typesafe.training.scalatrain.JourneyPlanner
import org.scalatest.{ Matchers, WordSpec }
import scala.collection.immutable.Seq

/**
 * Created by stevec on 2014-07-07.
 */
class JourneyPlannerSpec extends WordSpec with Matchers {

  val vancouver = Station("Vancouver")
  val portland = Station("Portland")
  val seattle = Station("Seattle")
  val eugene = Station("Eugene")

  val train1 = Train(InterCityExpress(1), Seq(Time(8, 0) -> vancouver, Time(11, 0) -> portland))
  val train2 = Train(RegionalExpress(2), Seq(Time(6, 0) -> portland, Time(10, 0) -> seattle))
  val train3 = Train(RegionalExpress(3), Seq(Time(6, 0) -> vancouver, Time(8, 0) -> seattle, Time(10, 0) -> portland))
  val train4 = Train(RegionalExpress(4), Seq(Time(6, 0) -> vancouver, Time(8, 0) -> seattle, Time(10, 0) -> portland, Time(12, 0) -> eugene))

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

  "JourneyPlannerisShortTrip" should {
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
}
