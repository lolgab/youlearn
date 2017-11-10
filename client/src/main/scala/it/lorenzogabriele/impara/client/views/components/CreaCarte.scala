package it.lorenzogabriele.impara.client.views

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.dropdown.UdashDropdown
import io.udash.bootstrap.dropdown.UdashDropdown.{DefaultDropdownItem, DropdownHeader}
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.UserService
import it.lorenzogabriele.impara.shared.model.db.tables._

import scala.concurrent.Future

class CreaCarte {
  import io.udash.seqFromElement

  import scalatags.JsDom.all._

  private val nuovoMazzo = "Nuovo Mazzo..."

  private val nessunMazzoSelezionato = "Nessun mazzo selezionato"

  private val mazzoDaCreare = Property("")

  private val mazzoSelezionato: Property[String] = Property(
    nessunMazzoSelezionato)

  private val creandoMazzo: Property[Boolean] = Property(false)

  private val mazzi: SeqProperty[Mazzo] = SeqProperty.empty[Mazzo]

  private val services = UserService.services()

  def downloadListaMazzi(): Unit = {
    val listaMazziFuture: Future[Seq[Mazzo]] =
      services.listaMazzi
    listaMazziFuture.foreach(seq => mazzi.set(seq.toList))
  }

  private val mazziDropdownItems: SeqProperty[DefaultDropdownItem] =
    SeqProperty.empty
  mazzi.listen(
    (mazzi: Seq[Mazzo]) =>
      mazziDropdownItems.set(
        mazzi.map(mazzo => DropdownHeader(mazzo.nome)) :+ DropdownHeader(
          nuovoMazzo)))
  private val mazziDropdown =
    UdashDropdown(mazziDropdownItems)(UdashDropdown.defaultItemFactory)(
      bind(mazzoSelezionato),
      BootstrapStyles.Button.btnPrimary) // TODO Aggiungere hover

  mazziDropdown.listen({
    case UdashDropdown.SelectionEvent(_, item) =>
      item match {
        case DropdownHeader(title) =>
          if (title == nuovoMazzo) {
            creandoMazzo.set(true)
            mazzoSelezionato.set("Nuovo mazzo...")
          } else {
            creandoMazzo.set(false)
            mazzoSelezionato.set(title)
          }
        case _ =>
      }
  })

  private val creandoMazzoForm = {
    val b = UdashButton()("Aggiungi Mazzo")
    val a = div(
      TextInput.debounced(mazzoDaCreare)(placeholder := "Nome nuovo mazzo"),
      b.render
    )
    b.listen({
      case _ =>
        val nomeMazzo: String = mazzoDaCreare.get
        services.nuovoMazzo(nomeMazzo).foreach { _ =>
          mazzoSelezionato.set(nomeMazzo)
          mazzoDaCreare.set("")
          creandoMazzo.set(false)
          downloadListaMazzi()
        }
    })
    a
  }

  downloadListaMazzi()

  def getMazzo: Option[Mazzo.Id] = {
    val m = mazzoSelezionato.get
    if (creandoMazzo.get || m == nessunMazzoSelezionato)
      None
    else mazzi.get.filter(_.nome == m).headOption.map(_.id)
  }

  private val content = div(
    mazziDropdown.render,
    br(),
    showIf(creandoMazzo)(creandoMazzoForm.render)
  )

  def getTemplate: Modifier = content
}
