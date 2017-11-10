package it.lorenzogabriele.impara.shared.model

import db.tables._

case class AuthToken(username: Utente.Username, token: Token)