package it.lorenzogabriele.impara.client

import io.udash._
import io.udash.utils.Bidirectional
import it.lorenzogabriele.impara.shared.model.db.tables.Mazzo

class RoutingRegistryDef extends RoutingRegistry[RoutingState] {
  def matchUrl(url: Url): RoutingState =
    url2State.applyOrElse(url.value.stripSuffix("/"),
                          (x: String) => ErrorState)

  def matchState(state: RoutingState): Url =
    Url(state2Url.apply(state))

  private val (url2State, state2Url) = Bidirectional[String, RoutingState] {
    case "/creacloze" => CreaClozeState
    case "/creadomandarisposta" => CreaDomandaRispostaState
    case "/registrazione" => RegistrazioneState
    case "/login" => LoginState
    case "/listamazzi" => ListaMazziState
    case "/studiamazzo" /:/ id => StudiaMazzoState(id)

    case "" => IndexState
  }
}
