package it.lorenzogabriele.impara.client

import io.udash.properties.{Invalid, Valid, ValidationResult}

object Utils {
  def validatorToBoolean(v: ValidationResult): Boolean = v match {
    case Valid => true
    case Invalid(_) => false
  }
}
