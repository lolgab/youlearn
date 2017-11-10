package it.lorenzogabriele.impara.client.views.studiaMazzo

import io.udash._
import it.lorenzogabriele.impara.client.{StudiaMazzoState, UserService}
import it.lorenzogabriele.impara.shared.model.db.tables.Mazzo
import it.lorenzogabriele.impara.shared.model.Carta
import it.lorenzogabriele.impara.client.Context._

class StudiaMazzoViewPresenter(m: String)
    extends ViewPresenter[StudiaMazzoState] {
  override def create() = {
    val model: ModelProperty[StudiaMazzoModel] = ModelProperty(
      StudiaMazzoModel(Mazzo(m.toInt,"",-1), false, Carta("", ""), -1)) // TODO toInt unsafe

    val presenter = new StudiaMazzoPresenter(model)

    val view = new StudiaMazzoView(model, presenter)

    (view, presenter)
  }
}
