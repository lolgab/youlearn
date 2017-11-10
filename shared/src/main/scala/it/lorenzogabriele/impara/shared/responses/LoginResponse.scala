package it.lorenzogabriele.impara.shared.responses

import it.lorenzogabriele.impara.shared.model.AuthToken

sealed trait LoginResponse extends Product with Serializable

case class LoginCompleted(token: AuthToken) extends LoginResponse
case object UsernameNotPresent extends LoginResponse
case object WrongPassword extends LoginResponse