package com.typesafe.training.scalatrain

import scala.collection.immutable.Seq

/**
 * Created by stevec on 2014-07-07.
 */
case class Train(
    info: TrainInfo,
    schedule: Seq[(Time, Station)]) {
  require(schedule.length >= 2)

  val stations: Seq[Station] = schedule.map(x => x._2)

  //TODO: verify that the schedule is strictly increasing in time
}

case class Station(
    name: String) {
}
