package it.lorenzogabriele.impara.shared.model

case class DomandaRisposta(domanda: String, risposta: String) extends Nota {
  val carte = List(Carta(domanda,risposta))
}