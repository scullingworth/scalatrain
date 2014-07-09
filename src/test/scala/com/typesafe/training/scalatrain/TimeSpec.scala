package com.typesafe.training.scalatrain

import org.scalatest.{ Matchers, WordSpec }

import scala.util.parsing.json.JSONObject

/**
 * Created by stevec on 2014-07-07.
 */
class TimeSpec extends WordSpec with Matchers {
  "Creating Time" should {
    "throw an IllegalArgumentException for hours not within 0 and 23" in {
      an[IllegalArgumentException] should be thrownBy Time(-1, 0)
      an[IllegalArgumentException] should be thrownBy Time(24, 0)
    }

    "throw an IllegalArgumentException for minutes not within 0 and 59" in {
      an[IllegalArgumentException] should be thrownBy Time(0, -1)
      an[IllegalArgumentException] should be thrownBy Time(0, 61)
    }

    "default arg for hours and minutes is 0" in {
      Time().minutes should equal(0)
      Time().hours should equal(0)
    }

    "asMinutes should be initialized correctly" in {
      Time().asMinutes should equal(0)
      Time(1, 1).asMinutes should equal(61)
    }

    "Calling minus or - should return the correct difference in minutes" in {
      Time(2, 2) - Time(1, 2) should equal(60)
      Time(2, 2).minus(Time(0, 2)) should equal(120)
      Time(23, 59) - Time(22, 0) should equal(119)
    }

    "Time.toString displays time in human-readable format" in {
      Time(9, 0).toString should equal("09:00")
      Time(23, 59).toString should equal("23:59")
      Time(0, 0).toString should equal("00:00")
    }

    "Time is ordered and comparisons work as expected" in {
      Time(9, 0) > Time(8, 59)
      Time(0, 0) < Time(0, 1)
      Time(1, 1) == Time(1, 1)
      Time(1, 1) >= Time(1, 1)
    }
  }

  "Time.toJson" should {
    "convert 9:00 to a JSONObject" in {
      Time(9, 0).toJson should equal(JSONObject(Map("hours" -> 9, "minutes" -> 0)))
    }
    "convert 0:00 to a JSONObject" in {
      Time(0, 0).toJson should equal(JSONObject(Map("hours" -> 0, "minutes" -> 0)))
    }
  }

  "Time.fromJson" should {
    "convert {hours : 9, minutes : 0} to 9:00" in {
      Time.fromJson(JSONObject(Map("hours" -> 9, "minutes" -> 0))) should equal(Some(Time(9, 0)))
    }
    "convert {hours : 9} to 9:00" in {
      Time.fromJson(JSONObject(Map("hours" -> 9))) should equal(Some(Time(9, 0)))
    }
    "convert {minutes :30} to 0:30" in {
      Time.fromJson(JSONObject(Map("minutes" -> 30))) should equal(Some(Time(0, 30)))
    }
    "convert {} to None" in {
      Time.fromJson(JSONObject(Map())) should equal(None)
    }
    "convert hours > 23 to None" in {
      Time.fromJson(JSONObject(Map("hours" -> 40, "minutes" -> 0))) should equal(None)
    }
    "convert invalid hours to None" in {
      Time.fromJson(JSONObject(Map("hours" -> "aa", "minutes" -> 0))) should equal(None)
    }
  }

}
