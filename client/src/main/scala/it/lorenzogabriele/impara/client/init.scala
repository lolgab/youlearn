package it.lorenzogabriele.impara.client

import io.udash._
import io.udash.properties.PropertyCreator
import io.udash.wrappers.jquery._
import it.lorenzogabriele.impara.shared.model.db.tables.Mazzo
import org.scalajs.dom.Element

object Context {
  implicit val executionContext =
    scalajs.concurrent.JSExecutionContext.Implicits.queue
  private val routingRegistry = new RoutingRegistryDef
  private val viewPresenterRegistry = new StatesToViewPresenterDef

  implicit val applicationInstance = new Application[RoutingState](
    routingRegistry,
    viewPresenterRegistry,
    RootState)

  import io.udash.rpc._
  import it.lorenzogabriele.impara.client.rpc._
  import it.lorenzogabriele.impara.shared.rpc._
  implicit val serverRpc: MainServerRPC =
    DefaultServerRPC[MainClientRPC, MainServerRPC](new RPCService)

  implicit val pcSeqMazzo: PropertyCreator[Seq[Mazzo]] =
    PropertyCreator.propertyCreator[Seq[Mazzo]]

  implicit val pcMazzo: PropertyCreator[Mazzo] = PropertyCreator.propertyCreator[Mazzo]

}

object Init extends StrictLogging {
  import Context._

  def main(args: Array[String]): Unit = {
    jQ((_: Element) => {
      val appRoot = jQ("#application").get(0)
      if (appRoot.isEmpty) {
        logger.error(
          "Application root element not found! Check your index.html file!")
      } else {
        applicationInstance.run(appRoot.get)

        import it.lorenzogabriele.impara.client.styles.{DemoStyles, GlobalStyles}
        import it.lorenzogabriele.impara.client.styles.partials.{FooterStyles, HeaderStyles}

        import scalacss.DevDefaults._
        import scalacss.ScalatagsCss._
        import scalatags.JsDom._
        jQ(
          GlobalStyles
            .render[TypedTag[org.scalajs.dom.raw.HTMLStyleElement]]
            .render).insertBefore(appRoot.get)
        jQ(
          DemoStyles
            .render[TypedTag[org.scalajs.dom.raw.HTMLStyleElement]]
            .render).insertBefore(appRoot.get)
        jQ(
          FooterStyles
            .render[TypedTag[org.scalajs.dom.raw.HTMLStyleElement]]
            .render).insertBefore(appRoot.get)
        jQ(
          HeaderStyles
            .render[TypedTag[org.scalajs.dom.raw.HTMLStyleElement]]
            .render).insertBefore(appRoot.get)
      }
    })
  }
}
