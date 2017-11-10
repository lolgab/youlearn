package it.lorenzogabriele.impara.client.styles.utils

import it.lorenzogabriele.impara.client.styles.constants.StyleConstants

import scala.language.postfixOps
import scalacss.DevDefaults._

object MediaQueries extends StyleSheet.Inline {
  import dsl._

  def tabletLandscape(properties: StyleA) = style(
    media.screen
      .minWidth(1 px)
      .maxWidth(StyleConstants.MediaQueriesBounds.TabletLandscapeMax px)(
        properties
      )
  )

  def tabletPortrait(properties: StyleA) = style(
    media.screen
      .minWidth(1 px)
      .maxWidth(StyleConstants.MediaQueriesBounds.TabletMax px)(
        properties
      )
  )

  def phone(properties: StyleA) = style(
    media.screen
      .minWidth(1 px)
      .maxWidth(StyleConstants.MediaQueriesBounds.PhoneMax px)(
        properties
      )
  )
}
