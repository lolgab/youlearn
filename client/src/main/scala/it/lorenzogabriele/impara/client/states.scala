package it.lorenzogabriele.impara.client

import io.udash._
import it.lorenzogabriele.impara.shared.model.db.tables._

sealed abstract class RoutingState(val parentState: RoutingState)
    extends State {
  def url(implicit application: Application[RoutingState]): String =
    s"#${application.matchState(this).value}"
}

case object RootState extends RoutingState(null)

case object IndexState extends RoutingState(RootState)

case object ErrorState extends RoutingState(RootState)

case object CreaClozeState extends RoutingState(RootState)

case object CreaDomandaRispostaState extends RoutingState(RootState)

case object RegistrazioneState extends RoutingState(RootState)

case object LoginState extends RoutingState(RootState)

case object InformazioniUtenteState extends RoutingState(RootState)

case object ListaMazziState extends RoutingState(RootState)

case class StudiaMazzoState(mazzo: String) extends RoutingState(RootState)