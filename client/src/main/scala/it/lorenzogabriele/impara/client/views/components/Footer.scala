package it.lorenzogabriele.impara.client.views.components
import it.lorenzogabriele.impara.client.styles.GlobalStyles
import it.lorenzogabriele.impara.client.styles.partials.FooterStyles
import org.scalajs.dom.raw.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

object Footer {
  private lazy val template = footer(FooterStyles.footer)(
    div(GlobalStyles.body)(
      div(FooterStyles.footerInner)(
        )
    )
  ).render

  def getTemplate: Element = template
}
