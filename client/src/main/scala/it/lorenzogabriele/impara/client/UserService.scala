package it.lorenzogabriele.impara.client
import io.udash._
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.shared.model.AuthToken
import it.lorenzogabriele.impara.shared.responses.{LoginCompleted, LoginResponse}
import it.lorenzogabriele.impara.shared.rpc.ServicesRPC
import org.scalajs.dom

import scala.concurrent.Future
import scala.util.Success

object UserService {
  private val browserStorageUserKey = "user"
  private val browserStorageTokenKey = "token"

  private val browserStorage: dom.Storage = dom.window.localStorage

  private val _token = Property[Option[AuthToken]](
    for {
      u <- Option(browserStorage.getItem(browserStorageUserKey))
      t <- Option(browserStorage.getItem(browserStorageTokenKey))
    } yield AuthToken(u, t)
  )

  val token: ReadableProperty[Option[AuthToken]] = _token.transform(identity)
    
  private val tokenUpdateListener: Registration = _token.listen {
    case Some(AuthToken(u, t)) =>
      browserStorage.setItem(browserStorageUserKey, u)
      browserStorage.setItem(browserStorageTokenKey, t)
    case None =>
      browserStorage.removeItem(browserStorageUserKey)
      browserStorage.removeItem(browserStorageTokenKey)
  }

  def login(username: String, password: String): Future[LoginResponse] = {
    val tokenRequest = serverRpc.auth().login(username, password)
    tokenRequest onComplete {
      case Success(ctx) =>
        ctx match {
          case LoginCompleted(token) =>
            println(token)
            _token.set(Some(token))
          case _ =>
            _token.set(None)
        }
      case _ =>
        _token.set(None)
    }
    tokenRequest
  }

  def services(): ServicesRPC = {
    _token.get match {
      case (Some(authToken)) => serverRpc.services(authToken)
      case _ => throw new Exception
    }

  }

  def logout(): Unit = {
    _token.get.foreach(serverRpc.services(_).logout())
    _token.set(None)
  }
}
