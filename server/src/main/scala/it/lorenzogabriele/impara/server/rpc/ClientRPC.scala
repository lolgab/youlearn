package it.lorenzogabriele.impara.server.rpc

import io.udash.rpc._
import it.lorenzogabriele.impara.shared.rpc._

import scala.concurrent.ExecutionContext

object ClientRPC {
  def apply(target: ClientRPCTarget)(
      implicit ec: ExecutionContext): MainClientRPC = {
    new DefaultClientRPC[MainClientRPC](target).get
  }
}
