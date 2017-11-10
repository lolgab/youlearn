package it.lorenzogabriele.impara.shared.rpc

import io.udash.rpc._
import it.lorenzogabriele.impara.shared.model.{AuthToken, Carta, Feedback}
import it.lorenzogabriele.impara.shared.model.db.tables._

import scala.concurrent.Future
import it.lorenzogabriele.impara.shared.responses.{LoginResponse, RegistrationResponse}

@RPC
trait MainServerRPC {
  def auth(): AuthenticationRPC
  def services(token: AuthToken): ServicesRPC
  def listaRegistrati: Future[Seq[String]]
}

@RPC
trait AuthenticationRPC {
  def login(user: String, password: String): Future[LoginResponse]
  def registrazione(user: String,
                    password: String,
                    nome: String,
                    cognome: String): Future[RegistrationResponse]
}

@RPC
trait ServicesRPC {
  def mazzoById(id: Int): Future[Mazzo]
  def listaMazzi: Future[Seq[Mazzo]]
  def nuovoMazzo(nome: String): Future[Mazzo.Id]
  def rimuoviMazzo(id: Mazzo.Id): Future[Unit]
  def aggiungiDaCompletare(testo: DaCompletare.Testo,
                           mazzo: Mazzo.Id): Future[Unit]
  def aggiungiDomandaRisposta(domanda: DomandaRisposta.Domanda,
                              risposta: DomandaRisposta.Risposta,
                              mazzo: Mazzo.Id): Future[Unit]
  def infoUtenteLoggato(): Future[Utente]
  def infoUtente(id: Utente.Id): Future[Utente]
  def prossimaCarta(mazzo: Mazzo.Id)
    : Future[Option[(it.lorenzogabriele.impara.shared.model.db.tables.Carta.Id, Carta)]]
  def feedbackCarta(carta: it.lorenzogabriele.impara.shared.model.db.tables.Carta.Id, feedback: Feedback): Future[Unit]
  def logout(): Unit
}
