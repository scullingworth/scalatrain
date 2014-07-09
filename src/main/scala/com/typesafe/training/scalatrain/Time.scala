package com.typesafe.training.scalatrain

import scala.util.parsing.json.JSONObject

/**
 * Created by stevec on 2014-07-07.
 */
case class Time(hours: Int = 0, minutes: Int = 0) extends Ordered[Time] {
  val asMinutes = (hours * 60) + minutes
  def minus(that: Time): Int = {
    this.asMinutes - that.asMinutes
  }
  def -(that: Time) = minus(that)

  require(hours >= 0 && hours <= 23)
  require(minutes >= 0 && minutes <= 59)

  override lazy val toString = f"$hours%02d:$minutes%02d"

  override def compare(that: Time): Int = {
    if (this.asMinutes == that.asMinutes) 0
    else if (this.asMinutes > that.asMinutes) 1
    else -1
  }

  def toJson: JSONObject = new JSONObject(Map("hours" -> hours, "minutes" -> minutes))
}

object Time {
  def fromMinutes(minutes: Int): Time = {
    Time(minutes / 60, minutes % 60)
  }

  def fromJson(jsonObj: JSONObject): Option[Time] = {
    val hours = jsonObj.obj.get("hours")
    val minutes = jsonObj.obj.get("minutes")

    try {
      (hours, minutes) match {
        case (Some(h: Int), Some(m: Int)) => Some(Time(h, m))
        case (Some(h: Int), None)         => Some(Time(h))
        case (None, Some(m: Int))         => Some(Time(0, m))
        case _                            => None
      }
    } catch {
      // we should probably log something here!
      case e => None
    }
  }
}