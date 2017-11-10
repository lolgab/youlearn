import Dependencies._
import UdashBuild._

name := "impara"

version in ThisBuild := "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.3"
organization in ThisBuild := "it.lorenzogabriele"
crossPaths in ThisBuild := false
scalacOptions in ThisBuild ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:existentials",
  "-language:dynamics",
  "-Xfuture",
  "-Xfatal-warnings",
  "-Xlint:-unused,_"
)

def crossLibs(configuration: Configuration) =
  libraryDependencies ++= crossDeps.value.map(_ % configuration)

val mainClassName = Some("it.lorenzogabriele.impara.server.Launcher")

lazy val impara = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, client, server)
  .dependsOn(server)
  .settings(
    publishArtifact := false,
    mainClass in Compile := mainClassName,
    mainClass in reStart := mainClassName
  )

lazy val shared = crossProject.crossType(CrossType.Pure)
  .in(file("shared")).disablePlugins(RevolverPlugin)
  .settings(
    crossLibs(Provided),
    EclipseKeys.useProjectId := true,
    EclipseKeys.eclipseOutput := Some(".target") 
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

lazy val server = project.in(file("server"))
  .dependsOn(sharedJVM)
  .disablePlugins(RevolverPlugin)
  .settings(
    libraryDependencies ++= backendDeps.value,
    crossLibs(Compile),
    compile := (compile in Compile).value,
    (compile in Compile) := (compile in Compile).dependsOn(copyStatics).value,
    copyStatics := IO.copyDirectory((crossTarget in client).value / StaticFilesDir, (target in Compile).value / StaticFilesDir),
    copyStatics := copyStatics.dependsOn(compileStatics in client).value,

    mappings in (Compile, packageBin) ++= {
      copyStatics.value
      ((target in Compile).value / StaticFilesDir).***.get map { file =>
        file -> file.getAbsolutePath.stripPrefix((target in Compile).value.getAbsolutePath)
      }
    },

    watchSources ++= (sourceDirectory in client).value.***.get,
    
    //Quill query probing
    unmanagedClasspath in Compile += baseDirectory.value / "src" / "main" / "resources", 

    sourceGenerators in Test += Def.task {
      val file = (sourceManaged in Test).value / "amm.scala"
      IO.write(file, """object amm extends App { ammonite.Main().run() }""")
      Seq(file)
    }.taskValue
  )

lazy val client = project.in(file("client"))
  .enablePlugins(ScalaJSPlugin, WorkbenchPlugin)
  .disablePlugins(RevolverPlugin)
  .dependsOn(sharedJS)
  .settings(
    libraryDependencies ++= frontendDeps.value,
    crossLibs(Compile),
    jsDependencies ++= frontendJSDeps.value,
    scalaJSUseMainModuleInitializer in Compile := true,

    compile := (compile in Compile).dependsOn(compileStatics).value,
    compileStatics := {
      IO.copyDirectory(sourceDirectory.value / "main/assets/fonts", crossTarget.value / StaticFilesDir / WebContent / "assets/fonts")
      IO.copyDirectory(sourceDirectory.value / "main/assets/images", crossTarget.value / StaticFilesDir / WebContent / "assets/images")
      val statics = compileStaticsForRelease.value
      (crossTarget.value / StaticFilesDir).***.get
    },

    artifactPath in(Compile, fastOptJS) :=
      (crossTarget in(Compile, fastOptJS)).value / StaticFilesDir / WebContent / "scripts" / "frontend-impl-fast.js",
    artifactPath in(Compile, fullOptJS) :=
      (crossTarget in(Compile, fullOptJS)).value / StaticFilesDir / WebContent / "scripts" / "frontend-impl.js",
    artifactPath in(Compile, packageJSDependencies) :=
      (crossTarget in(Compile, packageJSDependencies)).value / StaticFilesDir / WebContent / "scripts" / "frontend-deps-fast.js",
    artifactPath in(Compile, packageMinifiedJSDependencies) :=
      (crossTarget in(Compile, packageMinifiedJSDependencies)).value / StaticFilesDir / WebContent / "scripts" / "frontend-deps.js"
  )
