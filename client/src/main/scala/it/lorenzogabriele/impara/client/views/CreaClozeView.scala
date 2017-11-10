package it.lorenzogabriele.impara.client.views

import io.udash._
import io.udash.bootstrap.alert.{DismissibleUdashAlert}
import io.udash.bootstrap.button.UdashButton
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.{CreaClozeState, UserService}
import it.lorenzogabriele.impara.shared.model.DaCompletare
import it.lorenzogabriele.impara.shared.rpc.ServicesRPC
import org.scalajs.dom.html.{Div, Input}
import org.scalajs.dom.raw.KeyboardEvent
import org.scalajs.dom.window.alert

import scala.concurrent.Future

case object CreaClozeViewPresenter
    extends DefaultViewPresenterFactory[CreaClozeState.type](() => {

      val testo = Property[String]("")

      new CreaClozeView(testo)
    })

class CreaClozeView(testo: Property[String]) extends View {
  import scalatags.JsDom.all._

  val creaCarte = new CreaCarte()

  var n = 1

  val nota = testo.transform(DaCompletare(_))

  private val services: ServicesRPC = UserService.services()

  var selectionS = 0
  var selectionE = 0

  val input = TextArea.debounced(testo)(
    placeholder := "Inserisci il testo della carta...",
    autofocus := true,
    onkeydown := addBraces _,
    height := 50,
    width := 800
  )

  private def addBraces(e: KeyboardEvent): Unit = {
    val in = e.target.asInstanceOf[Input]
    selectionE = in.selectionEnd
    selectionS = in.selectionStart
    if (e.keyCode == 67 && e.ctrlKey && e.altKey) { // Ctrl+Alt+C
      val (newStr, caret) =
        wrapText(in.value, in.selectionStart, in.selectionEnd)

      testo.set(newStr)
      in.selectionEnd = caret
      in.selectionStart = caret
      n += 1
    }
  }

  def wrapText(s: String, start: Int, end: Int): (String, Int) = {
    val res = if (start == end) {
      val (f, l) = s.splitAt(start)
      val tmp = s"""$f{{c$n::}}$l"""
      (tmp, tmp.length - 2)
    } else {
      val (f, mTmp) = s.splitAt(start)
      val (m, l) = mTmp.splitAt(end - start)
      val tmp = s"""$f{{c$n::$m}}$l"""
      (tmp, tmp.length - l.length)
    }
    res
  }

  private def output(n: DaCompletare): Div = {
    div(
      if (n.isError)
        p("Errore di sintassi! Correggere per favore")
      else if (n.carte.isEmpty)
        p("Deve essere presente almeno una carta")
      else
        p("Carte generate:"),
      ul(n.carte.zipWithIndex.map {
        case (carta, indice) =>
          val i = indice + 1
          li(
            h4(s"Carta n° $i:"),
            h5(s"Domanda: ${carta.domanda}"),
            br(),
            h5(s"Risposta: ${carta.risposta}")
          )
      })
    ).render
  }

  val conferma = UdashButton()("Aggiungi carte")
  conferma.listen({ _ =>
    creaCarte.getMazzo match {
      case Some(m) =>
        val f: Future[Unit] = services.aggiungiDaCompletare(testo.get, m)
        f.foreach{ _ =>
          alerts.appendChild(DismissibleUdashAlert.success("Carte aggiunte con successo!").render)
          testo.set("")
        }
      case None => alert("Seleziona prima il mazzo")
    }
  })

  private val alerts = div().render

  private val content = div(
    creaCarte.getTemplate,
    input.render,
    produce(nota)(output),
    conferma.render,
    alerts
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {}
}
