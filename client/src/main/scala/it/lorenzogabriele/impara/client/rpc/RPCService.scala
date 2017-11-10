package it.lorenzogabriele.impara.client.rpc

import it.lorenzogabriele.impara.shared.rpc.MainClientRPC

class RPCService extends MainClientRPC {
  override def push(number: Int): Unit =
    println(s"Push from server: $number")
}
