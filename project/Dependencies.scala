import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {
  val udashVersion = "0.5.0"
  val udashJQueryVersion = "1.1.0"
  val bootstrapVersion = "3.3.7-1"
  val logbackVersion = "1.2.3"
  val jettyVersion = "9.3.11.v20160721"
  val fastParseVersion = "0.4.4"
  val macwireMacrosVersion = "2.3.0"
  val scalaCSSVersion = "0.5.3"

  val crossDeps = Def.setting(Seq[ModuleID](
    "io.udash" %%% "udash-core-shared" % udashVersion,
    "io.udash" %%% "udash-rpc-shared" % udashVersion,
    "com.lihaoyi" %%% "fastparse" % fastParseVersion,
    "com.softwaremill.macwire" %% "macros" % macwireMacrosVersion
  ))

  val frontendDeps = Def.setting(Seq[ModuleID](
    "io.udash" %%% "udash-core-frontend" % udashVersion,
    "io.udash" %%% "udash-jquery" % udashJQueryVersion,
    "io.udash" %%% "udash-rpc-frontend" % udashVersion,
    "io.udash" %%% "udash-bootstrap" % udashVersion,
    "com.github.japgolly.scalacss" %%% "core" % scalaCSSVersion,
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % scalaCSSVersion
  ))
  
  val frontendJSDeps = Def.setting(Seq[org.scalajs.sbtplugin.JSModuleID](
    "org.webjars" % "bootstrap" % bootstrapVersion / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js"
  ))


  val backendDeps = Def.setting(Seq[ModuleID](
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.eclipse.jetty" % "jetty-server" % jettyVersion,
    "org.eclipse.jetty" % "jetty-servlet" % jettyVersion,
    "io.udash" %% "udash-rpc-backend" % udashVersion,
    "org.eclipse.jetty.websocket" % "websocket-server" % jettyVersion,
    "com.github.t3hnar"  %% "scala-bcrypt" % "3.0",
    "org.postgresql" % "postgresql" % "9.4.1208",
    "io.getquill" %% "quill-jdbc" % "2.0.0",
    "io.getquill" %% "quill-async-mysql" % "2.0.0",
    "com.lihaoyi" % "ammonite" % "1.0.2" % "test" cross CrossVersion.full
  ))
}