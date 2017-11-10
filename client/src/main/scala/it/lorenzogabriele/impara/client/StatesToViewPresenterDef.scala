package it.lorenzogabriele.impara.client

import io.udash._
import it.lorenzogabriele.impara.client.views._
import it.lorenzogabriele.impara.client.views.studiaMazzo._
import it.lorenzogabriele.impara.shared.model.db.tables.Mazzo

class StatesToViewPresenterDef extends ViewPresenterRegistry[RoutingState] {
  def matchStateToResolver(
      state: RoutingState): ViewPresenter[_ <: RoutingState] = state match {
    case RootState                => RootViewPresenter
    case IndexState               => IndexViewPresenter
    case CreaClozeState           => CreaClozeViewPresenter
    case CreaDomandaRispostaState => CreaDomandaRispostaViewPresenter
    case RegistrazioneState       => RegistrazioneViewPresenter
    case LoginState               => LoginViewPresenter
    case InformazioniUtenteState  => InformazioniUtenteViewPresenter
    case ListaMazziState          => ListaMazziViewPresenter
    case StudiaMazzoState(m)      => new StudiaMazzoViewPresenter(m)
    case _                        => ErrorViewPresenter
  }
}
