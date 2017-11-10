package it.lorenzogabriele.impara.client.views

import io.udash._
import it.lorenzogabriele.impara.client.IndexState

object ErrorViewPresenter
    extends DefaultViewPresenterFactory[IndexState.type](() => new ErrorView)

class ErrorView extends View {
  import scalatags.JsDom.all._

  private val content = h3(
    "Qualcosa è andato storto :("
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {}
}
