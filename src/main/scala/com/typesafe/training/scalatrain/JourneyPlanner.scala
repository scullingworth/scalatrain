package com.typesafe.training.scalatrain

/**
 * Created by stevec on 2014-07-07.
 */
class JourneyPlanner(trains: Set[Train]) {
  import JourneyPlanner.Path

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
      case `from` +: `to` +: _      => true
      case `from` +: _ +: `to` +: _ => true
      case _                        => false
    })
  }

  def pathsBetweenTwoStations(from: Station, to: Station, departureTime: Time): Set[Path] = {

    def pathsBetweenTwoStationsWithoutCycles(from: Station, departureTime: Time, visitedStations: Set[Station]): Set[Path] = {
      val paths: Set[Path] = {
        if (from == to) Set(Seq())
        else {
          for {
            currentHop <- departingStationToHopsMap.getOrElse(from, Set()) if !visitedStations.contains(currentHop.from)
            path <- pathsBetweenTwoStationsWithoutCycles(currentHop.to, currentHop.arrivalTime, visitedStations + currentHop.from)
            if currentHop.departureTime >= departureTime
          } yield currentHop +: path
        }
      }
      paths
    }

    pathsBetweenTwoStationsWithoutCycles(from, departureTime, Set())
  }

}

object JourneyPlanner {
  type Path = Seq[Hop]

  def totalTime(path: Path): Int = {
    path.foldLeft(0)((acc: Int, hop: Hop) => acc + hop.duration)
  }

  def totalCost(path: Path): Int = {
    path.foldLeft(0)((acc: Int, hop: Hop) => acc + hop.cost)
  }

  def sortBy(paths: Set[Path], f: (Path) => Int): Seq[Path] = paths.toList.sortWith((path1: Path, path2: Path) => f(path2) - f(path1) > 0)

  def sortByTotalTime(paths: Set[Path]): Seq[Path] = {
    sortBy(paths, totalTime)
  }

  def sortByTotalCost(paths: Set[Path]): Seq[Path] = {
    sortBy(paths, totalCost)
  }
}