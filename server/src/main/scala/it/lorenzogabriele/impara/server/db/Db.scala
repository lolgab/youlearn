package it.lorenzogabriele.impara.server.db

import it.lorenzogabriele.impara.shared.model.db.tables.Carta.Id
import it.lorenzogabriele.impara.shared.model.db.tables._

import scala.collection.Seq
import scala.concurrent.Future

trait Db {
  def updateRipetizione(nextR: Ripetizione): Future[Unit]

  def getRipetizione(carta: Carta.Id, utente: Utente.Username): Future[Ripetizione]

  def mazzoById(id: Int): Future[Mazzo]

  def prossimaCarta(user: Utente.Username,
                    mazzo: Mazzo.Id): Future[(Ripetizione, CartaImpl)]

  def addUser(login: Utente.Username,
              password: Utente.Password,
              nome: Utente.Nome,
              cognome: Utente.Cognome): Future[Unit]
  def removeUser(login: Utente.Username): Future[Unit]
  def passwordOf(
      user: Utente.Username): Future[Option[Utente.Password]]
  def registeredUsers: Future[Seq[Utente]]
  def userIsRegistered(login: Utente.Username): Future[Boolean]
  def addMazzo(nome: Mazzo.Nome,
               creatore: Utente.Username): Future[Mazzo.Id]
  def rimuoviMazzo(id: Mazzo.Id): Future[Unit]
  def addTag(mazzo: Mazzo.Id, tag: Tag): Future[Unit]
  def mazziUtente(utente: Utente.Username): Future[Seq[Mazzo]]
  def aggiungiDaCompletare(testo: DaCompletare.Testo,
                           mazzo: Mazzo.Id): Future[Unit]
  def aggiungiDomandaRisposta(domanda: DomandaRisposta.Domanda,
                              risposta: DomandaRisposta.Risposta,
                              mazzo: Mazzo.Id): Future[Unit]
  def infoUtente(user: Utente.Username): Future[Utente]
  def infoUtente(id: Utente.Id): Future[Utente]
}
