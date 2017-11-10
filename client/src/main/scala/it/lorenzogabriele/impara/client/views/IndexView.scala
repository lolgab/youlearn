package it.lorenzogabriele.impara.client.views

import io.udash._
import it.lorenzogabriele.impara.client.IndexState

object IndexViewPresenter
    extends DefaultViewPresenterFactory[IndexState.type](() => new IndexView)

class IndexView extends View {
  import scalatags.JsDom.all._

  private val content = h3(
    "Index"
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {}
}
