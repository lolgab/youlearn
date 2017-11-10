package it.lorenzogabriele.impara.server

import it.lorenzogabriele.impara.shared.model.AuthToken

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random

object SessionToken {
  private type User = String
  private type Token = (String, Deadline)

  private val tokenLength = 30
  private val tokenDuration = 1.hour
  private val random: Random = new Random()

  private val tokens = mutable.Map[User, Token]()

  private def extendTokenDuration(user: String): Unit = {
    val (tokenStr, duration) = tokens(user)
    tokens.update(user, (tokenStr, tokenDuration.fromNow))
  }
  def generateAndGet(user: String): AuthToken = {
    val tokenStr = random.nextString(tokenLength)
    tokens.update(user, (tokenStr, tokenDuration.fromNow))
    AuthToken(user, tokenStr)
  }

  def get(user: String): Option[String] = { //TODO sostituire con checktoken!
    tokens.get(user).flatMap {
      case (tokenStr, deadline) =>
        if (!deadline.isOverdue()) {
          extendTokenDuration(user)
          Some(tokenStr)
        } else None
    }
  }

  def remove(user: String): Unit = {
    if (tokens.contains(user)) tokens.remove(user)
  }
}
