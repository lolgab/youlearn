package it.lorenzogabriele.impara.client.views

import io.udash._
import it.lorenzogabriele.impara.client.RootState
import it.lorenzogabriele.impara.client.styles.GlobalStyles
import it.lorenzogabriele.impara.client.views.components.{Footer, Header}
import org.scalajs.dom.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.tags2.main

object RootViewPresenter
    extends DefaultViewPresenterFactory[RootState.type](() => new RootView)

class RootView extends View {
  import scalatags.JsDom.all._

  private val child: Element = div().render

  private val content = div(
    Header.getTemplate,
    main(GlobalStyles.main)(
      div(GlobalStyles.body)(
        child
      )
    ),
    Footer.getTemplate
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {
    import io.udash.wrappers.jquery._
    jQ(child).children().remove()
    view.getTemplate.applyTo(child)
  }
}
