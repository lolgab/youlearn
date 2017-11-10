package it.lorenzogabriele.impara.server

import it.lorenzogabriele.impara.server.db.{Db, QuillDb}
import it.lorenzogabriele.impara.server.jetty.ApplicationServer

import scala.concurrent.ExecutionContext.Implicits.global

object Launcher {
  def main(args: Array[String]): Unit = {
    lazy val db: Db = new QuillDb()
    lazy val server =
      new ApplicationServer(8080, "server/target/UdashStatic/WebContent", db)
    server.start()
  }
}
