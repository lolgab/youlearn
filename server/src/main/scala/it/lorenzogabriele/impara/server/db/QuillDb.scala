package it.lorenzogabriele.impara.server.db

import java.sql.DatabaseMetaData
import java.util.Date

import io.getquill._
import it.lorenzogabriele.impara.shared.model.db.tables._

import scala.concurrent.{ExecutionContext, Future}

class QuillDb(implicit ec: ExecutionContext) extends Db {
  lazy val ctx = new MysqlAsyncContext(CamelCase, "ctx") // with QueryProbing

  import ctx._

  override def addUser(login: Utente.Username,
                       password: Utente.Password,
                       nome: Utente.Nome,
                       cognome: Utente.Cognome): Future[Unit] = {
    val q = quote {
      query[Utente]
        .insert(lift(Utente(0, login, password, nome, cognome)))
        .returning(_.id)
    }

    ctx.run(q).map(_ => ())
  }

  override def removeUser(login: Utente.Username): Future[Unit] = {
    val q = quote {
      query[Utente].filter(_.nome == lift(login)).delete
    }

    ctx.run(q).map(_ => ())
  }

  override def passwordOf(
      user: Utente.Username): Future[Option[Utente.Password]] = {
    val q = quote {
      for {
        utente <- query[Utente]
        if utente.username == lift(user)
      } yield utente.password
    }

    ctx.run(q).map(_.headOption)
  }

  override def registeredUsers: Future[Seq[Utente]] = {
    val q = quote {
      query[Utente]
    }

    ctx.run(q)
  }
  override def userIsRegistered(login: Utente.Username): Future[Boolean] = {
    val q = quote {
      for {
        utente <- query[Utente]
        if utente.username == lift(login)
      } yield utente
    }

    ctx.run(q).map(!_.isEmpty)
  }

  override def addMazzo(nome: Mazzo.Nome,
                        creatore: Utente.Username): Future[Mazzo.Id] = {
    val q1 = quote {
      for {
        u <- query[Utente]
        if u.username == lift(creatore)
      } yield u.id
    }

    def q2(id: Utente.Id) = quote {
      query[Mazzo].insert(lift(Mazzo(0, nome, id))).returning(_.id)
    }

    ctx
      .run(q1)
      .map(_.head) //TODO Not safe
      .flatMap(id => ctx.run(q2(id)))
  }

  def rimuoviMazzo(id: Mazzo.Id): Future[Unit] = {
    val q = quote {
      query[Mazzo].filter(_.id == lift(id)).delete
    }

    ctx.run(q).map(_ => ())
  }

  override def addTag(mazzo: Mazzo.Id, tag: Tag): Future[Unit] = {
    val q = quote {
      query[MazzoTag].insert(lift(MazzoTag(mazzo, tag)))
    }
    ctx.run(q).map(_ => ())
  }

  override def mazziUtente(utente: Utente.Username): Future[Seq[Mazzo]] = {
    val q = quote {
      for {
        u <- query[Utente]; if u.username == lift(utente)
        m <- query[Mazzo]; if m.creatore == u.id
      } yield m
    }

    ctx.run(q)
  }


  override def aggiungiDaCompletare(testo: DaCompletare.Testo,
                                    mazzo: Mazzo.Id): Future[Unit] = {
    /*
    val dc =
      it.lorenzogabriele.impara.shared.model.DaCompletare(testo).carte.length

    def q(mazzo: Mazzo.Id) = quote {
      query[Carta].insert(lift(Carta(0, mazzo))).returning(_.id)
    }

    def q2(id: Int, i: Int) = quote {
      query[DaCompletare].insert(lift(DaCompletare(id, testo, i)))
    }

    def rip(idCarta: Int) = {
      val u = quote {
        for {
          m <- query[Mazzo]
          if m.id == lift(mazzo)
        } yield m.creatore
      }
      quote {
        u.map(
          id =>
            query[Ripetizione].insert(Ripetizione(idCarta, lift(id), 0, lift(new Date()))))
      }
    }
    val seqFuture: Seq[Future[Unit]] = (0 until dc).map { i =>
      ctx
        .run(q(mazzo))
        .flatMap { id =>
          ctx.run(rip(id)); ctx.run(q2(id, i))
        }
        .map(_ => ())
    }

    Future.sequence(seqFuture).map(_ => ())
    */
    ???
  }


  override def aggiungiDomandaRisposta(domanda: DomandaRisposta.Domanda,
                                       risposta: DomandaRisposta.Risposta,
                                       mazzo: Mazzo.Id): Future[Unit] = {
    def q(mazzo: Mazzo.Id) = quote {
      query[Carta].insert(lift(Carta(0, mazzo))).returning(_.id)
    }

    def q2(id: Int) = quote {
      query[DomandaRisposta].insert(
        lift(DomandaRisposta(id, domanda, risposta)))
    }

    ctx.run(q(mazzo)).flatMap(id => ctx.run(q2(id))).map(_ => ())
  }

  override def infoUtente(user: Utente.Username): Future[Utente] = {
    val q = quote {
      for {
        u <- query[Utente]
        if u.username == lift(user)
      } yield u
    }

    ctx.run(q).map(_.head)
  }

  override def infoUtente(id: Utente.Id): Future[Utente] = {
    val q = quote {
      for {
        u <- query[Utente]
        if u.id == lift(id)
      } yield u

    }

    ctx.run(q).map(_.head) //TODO Unsafe
  }

  override def prossimaCarta(user: Utente.Username,
                             mazzo: Mazzo.Id): Future[(Ripetizione, CartaImpl)] = {
    val cartaId = quote {
      query[Utente]
        .filter(_.username == lift(user))
        .join(query[Ripetizione])
        .on(_.id == _.utente)
        .join(query[Carta])
        .on(_._2.carta == _.id)
        .join(query[Mazzo].filter(_.id == lift(mazzo)))
        .on(_._2.mazzo == _.id)
        .map(s => (s._1._1._2, s._1._2.id))
    }

    val daCompletare = quote {
      cartaId.join(query[DaCompletare]).on(_._2 == _.id).map(s => (s._1._1, s._2))
    }

    val domandaRisposta = quote {
      cartaId
        .join(query[DomandaRisposta])
        .on(_._2 == _.id)
        .map(s => (s._1._1, s._2)) //TODO avoid repetition
    }

    /*val daCompletare = quote {
      val cr = for {
        u <- query[Utente]
        r <- query[Ripetizione]
        c <- query[Carta]
        dc <- query[DaCompletare]
        if dc.id == c.id
        if u.username == lift(user)
        if c.mazzo == lift(mazzo)
        if r.carta == c.id
        if r.utente == u.id
      } yield (dc, r.prossimaRipetizione)
      cr.sortBy(_._2).map(_._1)
    }

    val domandaRisposta = quote {
      val cr = for {
        u <- query[Utente]
        r <- query[Ripetizione]
        c <- query[Carta]
        dr <- query[DomandaRisposta]
        if dr.id == c.id
        if u.username == lift(user)
        if c.mazzo == lift(mazzo)
        if r.carta == c.id
        if r.utente == u.id
      } yield (dr, r.prossimaRipetizione)
      cr.sortBy(_._2).map(_._1)
    }
     */
    /*

    val domandaRisposta = quote {
      for {
        dr <- query[DomandaRisposta]
        cid <- cartaRipetizione
        if dr.id == cid
      } yield dr
    }

    val daCompletare = quote {
      for {
        c <- query[DaCompletare]
        if c.id == cid
      } yield c
    }
     */
    ctx
      .run(daCompletare)
      .map(_.headOption)
      .flatMap(_ match {
        case Some(s) => Future.successful(s)
        case None    => ctx.run(domandaRisposta).map(_.head) //TODO unsafe
      })
  }

  def mazzoById(id: Int): Future[Mazzo] = {
    val q = quote(query[Mazzo].filter(_.id == lift(id)))

    ctx.run(q).map(_.head)
  }

  def getRipetizione(carta: Carta.Id,
                     utente: Utente.Username): Future[Ripetizione] = {
    val q = quote {
      for {
        u <- query[Utente]
        c <- query[Carta]
        r <- query[Ripetizione]
        if u.username == lift(utente)
        if c.id == r.carta
      } yield r
    }
    ctx.run(q).map(_.head)
  }

  def updateRipetizione(nextR: Ripetizione): Future[Unit] = {
    val q = quote {
      query[Ripetizione].update(lift(nextR))
    }
    ctx.run(q).map(_ => ())
  }

  /*  override def prossimaCarta(user: Utente.Username,
                             mazzo: Mazzo.Id): Future[CartaImpl] = {
    val cartaRipetizione = quote {
      val cr = for {
        u <- query[Utente]
        r <- query[Ripetizione]
        c <- query[Carta]
        if u.username == lift(user)
        if c.mazzo == lift(mazzo)
        if r.carta == c.id
        if r.utente == u.id
      } yield (c.id, r.prossimaRipetizione)
      cr.sortBy(_._2).map(_._1)
    }

    val domandaRisposta = quote {
      for {
        dr <- query[DomandaRisposta]
        cid <- cartaRipetizione
        if dr.id == cid
      } yield dr
    }

    val daCompletare = quote {
      for {
        c <- query[DaCompletare]
        cid <- cartaRipetizione
        if c.id == cid
      } yield c
    }

    ctx
      .run(domandaRisposta)
      .map(_.headOption)
      .flatMap(_ match {
        case Some(s) => Future.successful(s)
        case None    => ctx.run(daCompletare).map(_.head) //TODO unsafe
      })
  }
 */
}
