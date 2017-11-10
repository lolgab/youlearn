package it.lorenzogabriele.impara.client.views

import io.udash._
import io.udash.bootstrap.button._
import io.udash.bootstrap.table._
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.{IndexState, ListaMazziState, StudiaMazzoState, UserService}
import it.lorenzogabriele.impara.shared.model.db.tables.{Mazzo, Utente}
import org.scalajs.dom.raw.Event

import scala.concurrent.Future
import scalatags.JsDom.all._

case class ListaMazziModel(mazziUtente: Seq[Mazzo], nuovoMazzo: String, utente: Option[Utente])

object ListaMazziModel {
  lazy val empty = new ListaMazziModel(Nil, "", None)
}

class ListaMazziPresenter(model: ModelProperty[ListaMazziModel]) extends Presenter[ListaMazziState.type] {
  downloadMazziUtente()
  downloadUtente()

  override def handleState(state: ListaMazziState.type): Unit = ()

  def downloadMazziUtente(): Unit = {
    val mazziUtenteFuture: Future[Seq[Mazzo]] = UserService.services().listaMazzi
    mazziUtenteFuture.foreach{ m =>
      model.subProp(_.mazziUtente).set(m)
    }
  }

  def downloadUtente(): Unit = {
    UserService.services().infoUtenteLoggato().foreach { u =>
      model.subProp(_.utente).set(Some(u))
    }
  }
}

object ListaMazziViewPresenter extends ViewPresenter[ListaMazziState.type] {
  override def create(): (View, Presenter[ListaMazziState.type]) = {
    val model: ModelProperty[ListaMazziModel] =
      ModelProperty(ListaMazziModel.empty)

    val presenter = new ListaMazziPresenter(model)
    val view = new ListaMazziView(model, presenter)

    (view, presenter)
  }
}

class ListaMazziView(model: ModelProperty[ListaMazziModel], presenter: ListaMazziPresenter) extends View {
  private def content = div(
    h2("Mazzi"),
    tabellaMazzi,
    aggiungiMazzo
  )

  private def tabellaMazzi = {
    val striped = Property(true)
    val bordered = Property(true)
    val hover = Property(true)
    val condensed = Property(false)
    val items = model.subSeq(_.mazziUtente)
    val table = UdashTable(striped, bordered, hover, condensed)(items)(
      headerFactory =
        Some(() => tr(th(b("Nome")), th(b("Creatore")), th(b(""))).render),
      rowFactory = mazzo =>
        tr(
          td(produce(mazzo)(v => i(v.nome).render)),
          td(produce(model.subProp(_.utente))
              (u => i(s"${u.map(_.nome).getOrElse("")} ${u.map(_.cognome).getOrElse("")}").render)),
          td(produce(mazzo){v => 
              val b = UdashButton()("X")
              b.listen { case _ =>
                val s = UserService.services()
                println(s"services: $s")
                s.rimuoviMazzo(v.id).foreach{ _ =>
                  presenter.downloadMazziUtente()
                  model.subProp(_.nuovoMazzo).set("")
                }
              }
              b.render            
            },
        ),
        onclick := {
          e: Event => applicationInstance.goTo(new StudiaMazzoState(mazzo.get.id.toString))
        }
      ).render
    )
    table.render
  }
  
  private def aggiungiMazzo = {
    val b = UdashButton()("Aggiungi mazzo")
    b.listen {
      case _ =>
        UserService.services().nuovoMazzo(model.subProp(_.nuovoMazzo).get).foreach { _ =>
          presenter.downloadMazziUtente()
        }
    }
    div(
      TextInput.debounced(model.subProp(_.nuovoMazzo))(placeholder := "Nome nuovo mazzo..."),
      br(),
      b.render
    )
  }

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {}
}
