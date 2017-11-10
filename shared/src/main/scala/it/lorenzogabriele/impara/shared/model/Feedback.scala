package it.lorenzogabriele.impara.shared.model

import it.lorenzogabriele.impara.shared.model.db.tables.Ripetizione.Step

sealed trait Feedback {
  val nome: String
  def nextStep(step: Step): Step
}

case object Ripeti extends Feedback {
  override val nome: String = "Ripeti"
  override def nextStep(step: Step): Step = 1
}
case object Normale extends Feedback {
  override val nome: String = "Normale"
  override def nextStep(step: Step): Step = step + 1
}
case object Facile extends Feedback {
  override val nome: String = "Facile"
  override def nextStep(step: Step): Step = step + 2
}
