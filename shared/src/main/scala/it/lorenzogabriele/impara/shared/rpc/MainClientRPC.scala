package it.lorenzogabriele.impara.shared.rpc

import io.udash.rpc._

@RPC
trait MainClientRPC {
  def push(number: Int): Unit
}
       