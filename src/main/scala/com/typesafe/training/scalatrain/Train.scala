package com.typesafe.training.scalatrain

import scala.collection.immutable.Seq
import com.github.nscala_time.time.Imports._

/**
 * Created by stevec on 2014-07-07.
 */
case class Train(
    info: TrainInfo,
    schedule: Seq[(Time, Station)],
    daysOperating: Set[WeekDays.Value] = WeekDays.values,
    exceptionDates: Set[DateTime] = Set()) {
  require(schedule.length >= 2)

  val stations: Seq[Station] = schedule.map(x => x._2)

  //TODO: verify that the schedule is strictly increasing in time

  def backToBackStations: Seq[(Station, Station)] = {
    def buildStationTuple(stations: Seq[Station]): Seq[(Station, Station)] = {
      stations match {
        case h :: t :: Nil => Seq((h, t))
        case h :: t        => (h, t.head) +: buildStationTuple(t)
      }
    }

    buildStationTuple(schedule.map(x => x._2))
  }

  val departureTimes: Seq[(Station, Time)] = schedule map (stop => (stop._2, stop._1))

  def isRunningOn(date: DateTime): Boolean = !isAnExceptionDate(date) && runsOnOperatingDays(date)

  private def isAnExceptionDate(date: DateTime): Boolean = {
    exceptionDates.exists(exceptionDate => {
      val (exceptionYear, exceptionMonth, exceptionDay) = (exceptionDate.getYear, exceptionDate.monthOfYear, exceptionDate.dayOfMonth)
      val (year, month, day) = (date.getYear(), date.monthOfYear(), date.dayOfMonth())
      exceptionYear == year && exceptionMonth == month && exceptionDay == day
    })
  }

  private def runsOnOperatingDays(date: DateTime): Boolean = {
    println("date: " + date)
    
    daysOperating exists (day => {
      println(s"day.id: ${day.id}; date.dayOfWeek().get(): ${date.dayOfWeek().get()}")
      day.id == date.dayOfWeek().get()})
  }

}

case class Station(
    name: String) {
}

case class Hop(from: Station, to: Station, train: Train, cost: Int = 0) {
  require(train.schedule exists (s => s._2 == from))
  require(train.schedule exists (s => s._2 == to))
  require(train.schedule.dropWhile(s => s._2 != from).tail.exists(t => t._2 == to))

  private def getTimeForStation(station: Station): Time = {
    (train.departureTimes find (stationAndTime => stationAndTime._1 == station)).get._2
  }
  val departureTime: Time = getTimeForStation(from)
  val arrivalTime: Time = getTimeForStation(to)
  val duration: Int = arrivalTime.asMinutes - departureTime.asMinutes

  override def toString: String = s"from: $from; to: $to; train#: ${train.info.number}"
}

object WeekDays extends Enumeration {
  type WeekDay = Value
  val Sun, Mon, Tues, Wed, Thurs, Fri, Sat = Value
}
