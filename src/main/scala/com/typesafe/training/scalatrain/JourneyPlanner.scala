package com.typesafe.training.scalatrain

/**
 * Created by stevec on 2014-07-07.
 */
class JourneyPlanner(trains: Set[Train]) {
  val stations: Set[Station] = trains.flatMap(x => x.stations)

  def trainsAt(station: Station): Set[Train] = trains filter (train => train.stations contains station)

  def stopsAt(station: Station): Set[(Time, Train)] = {
    for {
      train <- trains
      (time, stat) <- train.schedule if stat == station
    } yield (time, train)
  }

  def isShortTrip(from: Station, to: Station): Boolean = {
    trains.exists(train => train.stations.dropWhile(s => s != from) match {
      case `from` +: `to` +: _      => true
      case `from` +: _ +: `to` +: _ => true
      case _                        => false
    })
  }
}