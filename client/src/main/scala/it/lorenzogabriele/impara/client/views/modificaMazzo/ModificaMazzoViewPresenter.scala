package it.lorenzogabriele.impara.client.views.modificaMazzo

import io.udash._
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.StudiaMazzoState
import it.lorenzogabriele.impara.shared.model.db.tables.Mazzo

class ModificaMazzoViewPresenter(m: Mazzo) extends ViewPresenter[StudiaMazzoState] {
  override def create() = {
    val model: ModelProperty[ModificaMazzoModel] = ModelProperty(ModificaMazzoModel(m))

    val presenter = new ModificaMazzoPresenter(model)

    val view = new ModificaMazzoView(model, presenter)  

    (view, presenter)
  }
}