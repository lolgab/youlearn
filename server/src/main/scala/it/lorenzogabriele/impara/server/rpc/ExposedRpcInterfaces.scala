package it.lorenzogabriele.impara.server.rpc

import java.util.Date
import java.time.Instant
import java.time.temporal.ChronoUnit

import com.github.t3hnar.bcrypt._
import io.udash.rpc._
import it.lorenzogabriele.impara.server.SessionToken
import it.lorenzogabriele.impara.server.db.Db
import it.lorenzogabriele.impara.server.repetition.Repetition
import it.lorenzogabriele.impara.shared.model.{AuthToken, Feedback}
import it.lorenzogabriele.impara.shared.model
import it.lorenzogabriele.impara.shared.model.db.tables._
import it.lorenzogabriele.impara.shared.responses._
import it.lorenzogabriele.impara.shared.rpc._
import org.joda.time.DateTime

import scala.collection.mutable.WeakHashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class MainServerRPCImpl(db: Db)(implicit clientId: ClientId)
    extends MainServerRPC {
  val servicesInstances =
    WeakHashMap.empty[Utente.Username, ServicesRPC]

  override def auth(): AuthenticationRPC = {
    new AuthenticationRPCImpl(db)
  }

  override def services(token: AuthToken): ServicesRPC = {
    lazy val services = new ServicesRPCImpl(token.username, db)
    if (SessionToken.get(token.username).contains(token.token))
      if (!servicesInstances.contains(token.username)) {
        servicesInstances += token.username -> services
        services
      } else servicesInstances(token.username)
    else
      throw new Exception
  }

  override def listaRegistrati: Future[Seq[String]] =
    db.registeredUsers.map(_.map(_.nome))
}

class AuthenticationRPCImpl(db: Db)(implicit clientId: ClientId)
    extends AuthenticationRPC {
  override def login(user: String, password: String): Future[LoginResponse] = {
    db.passwordOf(user).map {
      case Some(p) =>
        if (password.isBcrypted(p)) {
          println(s"utente $user loggato")
          LoginCompleted(SessionToken.generateAndGet(user))
        } else
          WrongPassword
      case None => UsernameNotPresent
    }
  }

  override def registrazione(user: String,
                             password: String,
                             nome: String,
                             cognome: String): Future[RegistrationResponse] = {
    db.userIsRegistered(user).flatMap {
      if (_) {
        println(s"utente $user già registrato")
        Future(UsernameAlreadyPresent)
      } else {
        println(s"utente $user registrato con password $password")
        db.addUser(user, password.bcrypt, nome, cognome)
          .map(b => RegistrationCompleted)
      }
    }
  }

}

class ServicesRPCImpl(user: String, db: Db)(implicit clientId: ClientId)
    extends ServicesRPC {
  def listaMazzi: Future[Seq[Mazzo]] = db.mazziUtente(user)

  def nuovoMazzo(nome: String): Future[Mazzo.Id] =
    db.addMazzo(nome, user)

  def aggiungiDaCompletare(testo: DaCompletare.Testo,
                           mazzo: Mazzo.Id): Future[Unit] = {

    // db.aggiungiDaCompletare(testo, mazzo)
    ???
  }

  def aggiungiDomandaRisposta(domanda: DomandaRisposta.Domanda,
                              risposta: DomandaRisposta.Risposta,
                              mazzo: Mazzo.Id): Future[Unit] =
    db.aggiungiDomandaRisposta(domanda, risposta, mazzo)

  def infoUtenteLoggato(): Future[Utente] =
    db.infoUtente(user).map(_.copy(password = ""))

  def infoUtente(id: Utente.Id): Future[Utente] =
    db.infoUtente(id).map(_.copy(password = ""))
  def logout(): Unit = {
    SessionToken.remove(user)
    println(s"utente $user logout")
  }

  def rimuoviMazzo(id: Mazzo.Id): Future[Unit] = {
    println(s"rimuovendo mazzo: $id")
    db.mazziUtente(user).map(_.exists(_.id == id)).map {
      if (_) db.rimuoviMazzo(id)
      else ()
    }
  }

  def prossimaCarta(mazzo: Mazzo.Id): Future[
    Option[(Carta.Id, it.lorenzogabriele.impara.shared.model.Carta)]] = {
    db.prossimaCarta(user, mazzo).map {
      case (rip, carta) =>
        if(rip.prossimaRipetizione.after(Date.from(Instant.from(new Date(new Date().getTime  + 24 * 60 * 60 * 1000).toInstant).truncatedTo(ChronoUnit.DAYS)))) {
          None
        } else
          Some(carta match {
          case DomandaRisposta(id, domanda, risposta) =>
            (id, it.lorenzogabriele.impara.shared.model.Carta(domanda, risposta))
          case DaCompletare(id, testo, numero) =>
            (id,
              it.lorenzogabriele.impara.shared.model
                .DaCompletare(testo)
                .carte(numero))
        })
    }
  }
  def mazzoById(id: Int): Future[Mazzo] = {
    db.mazzoById(id)
  }

  def feedbackCarta(
      carta: it.lorenzogabriele.impara.shared.model.db.tables.Carta.Id,
      feedback: Feedback): Future[Unit] = {
    db.getRipetizione(carta, user).map { (r: Ripetizione) =>
      val nextR = Repetition.nextRepetition(r, feedback)
      db.updateRipetizione(nextR)
    }

  }

}
