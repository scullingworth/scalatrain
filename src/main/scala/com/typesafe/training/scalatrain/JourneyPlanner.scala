package com.typesafe.training.scalatrain

/**
 * Created by stevec on 2014-07-07.
 */
class JourneyPlanner(trains: Set[Train]) {
  val stations: Set[Station] = trains.flatMap(x => x.stations)

  val departingStationToHopsMap: Map[Station, Set[Hop]] = {
    val result = for {
      train <- trains
      (departureStation, arrivalStation) <- train.backToBackStations
    } yield Hop(departureStation, arrivalStation, train)
    result.groupBy(_.from)
  }

  def trainsAt(station: Station): Set[Train] = trains filter (train => train.stations contains station)

  def stopsAt(station: Station): Set[(Time, Train)] = {
    for {
      train <- trains
      (time, stat) <- train.schedule if stat == station
    } yield (time, train)
  }

  def isShortTrip(from: Station, to: Station): Boolean = {
    trains.exists(train => train.stations.dropWhile(s => s != from) match {
      case `from` +: `to` +: _ => true
      case `from` +: _ +: `to` +: _ => true
      case _ => false
    })
  }

  def pathsBetweenTwoStations(from: Station, to: Station, departureTime: Time): Set[Seq[Hop]] = {
    val paths: Set[Seq[Hop]] = {
      if (from == to) Set(Seq())
      else {
        for {
          currentHop <- departingStationToHopsMap.getOrElse(from, Set())
          path <- pathsBetweenTwoStations(currentHop.to, to, departureTime) if currentHop.departureTime >= departureTime
        } yield currentHop +: path
      }
    }
    paths filter (path => !containsImpossibleConnection(path))
  }

  def containsImpossibleConnection(hops: Seq[Hop]): Boolean = {
    if (hops.isEmpty) false
    else {
      val hopPairs = hops zip hops.tail
      hopPairs exists {
        case (firstHop, secondHop) => firstHop.arrivalTime > secondHop.departureTime
      }
    }
  }

}