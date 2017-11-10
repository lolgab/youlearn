package it.lorenzogabriele.impara.client.views.studiaMazzo

import io.udash._
import it.lorenzogabriele.impara.client.{StudiaMazzoState, UserService}
import it.lorenzogabriele.impara.shared.model.{Carta, Feedback}
import it.lorenzogabriele.impara.shared.model.db.tables.{
  DaCompletare,
  DomandaRisposta
}
import it.lorenzogabriele.impara.client.Context._

import scala.concurrent.Future

class StudiaMazzoPresenter(model: ModelProperty[StudiaMazzoModel])
    extends Presenter[StudiaMazzoState] {

  override def handleState(state: StudiaMazzoState): Unit = ()

  val services = UserService.services()

  downloadMazzo(model.subProp(_.mazzo.id).get).foreach(_ =>
    downloadProssimaCarta())

  def downloadMazzo(m: Int): Future[Unit] = {
    services.mazzoById(m).map { mazzo =>
      model.subProp(_.mazzo).set(mazzo)
    }
  }

  def downloadProssimaCarta(): Unit = {
    val m = model.subProp(_.mazzo).get
    services.prossimaCarta(m.id).foreach {
      case None => model.subProp(_.ripetizioniTerminate).set(true)
      case Some((id, c)) =>
        println(s"prossima carta: id = $id, c = $c")
        model.subProp(_.carta).set(c)
        model.subProp(_.idCarta).set(id)
        model.subProp(_.mostraRisposta).set(false)
        model.subProp(_.ripetizioniTerminate).set(false)
    }
  }

  def mandaFeedback(feedback: Feedback): Unit = {
    val carta = model.subProp(_.idCarta).get
    services
      .feedbackCarta(carta, feedback)
      .foreach(_ => downloadProssimaCarta())
  }
}
