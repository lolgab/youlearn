package it.lorenzogabriele.impara.client.views.components
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.button.UdashButton.ButtonClickEvent
import it.lorenzogabriele.impara.client.styles.GlobalStyles
import it.lorenzogabriele.impara.client.styles.partials.HeaderStyles
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.RoutingState

object Header {
  var state = List[RoutingState]()
  applicationInstance.onStateChange(state ::= _.oldState)

  val b = UdashButton()("Indietro")
  b.listen {
    case _ =>
      state.headOption match {
        case None =>
        case Some(s) =>
          applicationInstance.goTo(s)
      }
  }

  private lazy val template = header(HeaderStyles.header)(
    div(GlobalStyles.body, GlobalStyles.clearfix)(
      div(HeaderStyles.headerLeft)(
        a(HeaderStyles.headerLogo)(
          Image("udash_logo_m.png", "Impara", GlobalStyles.block),
        ),
        b.render
      ),
      div(HeaderStyles.headerRight)(
        //TODO Login
      )
    )
  ).render

  def getTemplate: Element = template
}
