package it.lorenzogabriele.impara.client.views

import io.udash._
import io.udash.bootstrap.alert.DismissibleUdashAlert
import io.udash.bootstrap.button.UdashButton
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.{CreaDomandaRispostaState, UserService}
import it.lorenzogabriele.impara.shared.model.db.tables._
import it.lorenzogabriele.impara.shared.rpc.ServicesRPC
import org.scalajs.dom.html.Div
import org.scalajs.dom.window.{alert, localStorage}

import scala.concurrent.Future

case class DomandaRispostaModel(domanda: String, risposta: String)
object DomandaRispostaModel {
  def empty = new DomandaRispostaModel("", "")
}

case object CreaDomandaRispostaViewPresenter
    extends DefaultViewPresenterFactory[CreaDomandaRispostaState.type](() => {

      val model: ModelProperty[DomandaRispostaModel] =
        ModelProperty(DomandaRispostaModel.empty)

      new CreaDomandaRispostaView(model)
    })

class CreaDomandaRispostaView(model: ModelProperty[DomandaRispostaModel])
    extends View {
  import scalatags.JsDom.all._
  //import io.udash.seqFromElement

  val creaCarte = new CreaCarte()

  val user = localStorage.getItem("user")
  val token = localStorage.getItem("token")

  private val services: ServicesRPC = UserService.services()

  private val domanda = model.subProp(_.domanda)
  private val risposta = model.subProp(_.risposta)

  private val domandaInput = TextArea.debounced(domanda)(
    placeholder := "Inserisci la domanda...",
    autofocus := true,
    height := 50,
    width := 800
  )

  private val rispostaInput = TextArea.debounced(risposta)(
    placeholder := "Inserisci la risposta...",
    autofocus := true,
    height := 50,
    width := 800
  )

  private def output(model: DomandaRispostaModel): Div = {
    div(
      h5(s"Domanda: ${model.domanda}"),
      br(),
      h5(s"Risposta: ${model.risposta}")
    ).render
  }

  val conferma = UdashButton()("Aggiungi carta")
  conferma.listen({ _ =>
    creaCarte.getMazzo match {
      case Some(m) =>
        val f: Future[Unit] = services.aggiungiDomandaRisposta(domanda.get, risposta.get, m)
        f.foreach{ _ =>
          alerts.appendChild(DismissibleUdashAlert.success("Carte aggiunte con successo!").render)
          domanda.set("")
          risposta.set("")
        }
      case None => alert("Seleziona prima il mazzo")
    }
  })

  private val alerts = div().render

  private val content = div(
    creaCarte.getTemplate,
    br(),
    domandaInput.render,
    br(),
    br(),
    rispostaInput.render,
    produce(model)(output),
    br(),
    conferma.render,
    alerts
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {}
}
