package it.lorenzogabriele.impara.shared.responses

sealed trait RegistrationResponse

case object RegistrationCompleted extends RegistrationResponse
case object UsernameAlreadyPresent extends RegistrationResponse