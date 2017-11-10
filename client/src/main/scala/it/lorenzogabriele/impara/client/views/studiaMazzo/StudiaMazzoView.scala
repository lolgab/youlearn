package it.lorenzogabriele.impara.client.views.studiaMazzo

import io.udash._
import io.udash.bootstrap.button.UdashButton
import it.lorenzogabriele.impara.shared.model.{Facile, Feedback, Normale, Ripeti}

import scalatags.JsDom.all._

class StudiaMazzoView(model: ModelProperty[StudiaMazzoModel],
                      presenter: StudiaMazzoPresenter)
    extends View {
  val mostraRisposta = model.subProp(_.mostraRisposta)
  val carta = model.subProp(_.carta)

  def content = div(
    produce(model.subProp(_.ripetizioniTerminate))(
      if(_) h3("Per oggi hai terminato lo studio di questo mazzo.").render
      else span(
        h3("Domanda"),
        produce(carta){c =>
          val elem = h4().render
          elem.innerHTML = c.domanda
          elem
        }, //TODO Brutto!!
        showIf(mostraRisposta.transform(!_))(mostraRispostaButton),
        produce(model)(c => if(c.mostraRisposta) {
          val elem = h4().render
          elem.innerHTML = c.carta.risposta // TODO DIY!!!
          Seq(h3("Risposta").render, elem, pulsantiFeedback)
        } else Seq.empty)
      ).render
    )
  )

  private def mostraRispostaButton = {
    val b = UdashButton()("Mostra risposta")
    b.listen {
      case _ =>
        model.subProp(_.mostraRisposta).set(true)
    }
    b.render
  }

  private def pulsantiFeedback = {
    def creaButton(feedback: Feedback) = {
      val b = UdashButton()(feedback.nome)
      b.listen({
        case _ =>
          presenter.mandaFeedback(feedback)
          println(feedback.nome)
      })
      b.render
    }
    div(
      creaButton(Ripeti),
      creaButton(Normale),
      creaButton(Facile)
    ).render
  }


  def getTemplate: scalatags.generic.Modifier[org.scalajs.dom.Element] = content
  def renderChild(view: io.udash.core.View): Unit = ???
}
