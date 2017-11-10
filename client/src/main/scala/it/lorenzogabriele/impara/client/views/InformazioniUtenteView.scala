package it.lorenzogabriele.impara.client.views

import io.udash._
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.InformazioniUtenteState
import it.lorenzogabriele.impara.shared.model.db.tables.Utente

import scalatags.JsDom.all._

case class InformazioniUtenteModel(utente: Utente)

object InformazioniUtenteViewPresenter
    extends DefaultViewPresenterFactory[InformazioniUtenteState.type](() => {
      val model: ModelProperty[InformazioniUtenteModel] = ModelProperty(null) //TODO !!!
      new InformazioniUtenteView(model)
    })

class InformazioniUtenteView(model: ModelProperty[InformazioniUtenteModel])
    extends View {
  private def content = div(
    h2(s"Infomrazioni Profilo")
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {}
}
