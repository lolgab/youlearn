package it.lorenzogabriele.impara.server.repetition

import it.lorenzogabriele.impara.shared.model.db.tables.Ripetizione

import scala.concurrent.duration._
import java.util.Date

import it.lorenzogabriele.impara.shared.model.Feedback

object Repetition {
  private val stepDuration: Map[Int, Duration] =
    List(25.seconds, 2.minutes, 10.minutes, 1.days, 5.days, 25.days).zipWithIndex
      .map {
        case (d, i) => (i, d)
      }
      .toMap
      .withDefault { i =>
        require(i >= 0) //TODO unsafe
        (4*30).days
      }

  def nextRepetition(ripetizione: Ripetizione,
                     risposta: Feedback): Ripetizione = {
    val newStep = risposta.nextStep(ripetizione.step)

    val prossimaRipetizione = new Date(
      ripetizione.prossimaRipetizione.getTime + stepDuration(newStep).toMillis)
    ripetizione.copy(step = newStep,
                     prossimaRipetizione = prossimaRipetizione)
  }
}