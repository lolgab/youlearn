package it.lorenzogabriele.impara.shared.model.db

import java.util.Date

object tables {
  case class Utente(id: Utente.Id,
                    username: Utente.Username,
                    password: Utente.Password,
                    nome: Utente.Nome,
                    cognome: Utente.Cognome)
  object Utente {
    type Id       = Int
    type Username = String
    type Password = String
    type Nome     = String
    type Cognome  = String
  }
  case class Mazzo(id: Mazzo.Id,
                   nome: Mazzo.Nome,
                   creatore: Utente.Id)
  object Mazzo {
    type Id   = Int
    type Nome = String
  }

  case class IscrizioneMazzo(utente: Utente.Id, mazzo: Mazzo.Id)

  type Tag = String
  case class IscrizioneTag(utente: Utente.Id, tag: Tag)

  case class MazzoTag(mazzo: Mazzo.Id, tag: Tag)

  sealed trait CartaImpl //TODO find new name

  case class DaCompletare(id: Carta.Id,
                          testo: DaCompletare.Testo,
                          numero: DaCompletare.Numero)
      extends CartaImpl
  object DaCompletare {
    type Id     = Int
    type Testo  = String
    type Numero = Int
  }

  case class DomandaRisposta(id: Carta.Id,
                             domanda: DomandaRisposta.Domanda,
                             risposta: DomandaRisposta.Risposta)
      extends CartaImpl
  object DomandaRisposta {
    type Domanda  = String
    type Risposta = String
  }

  case class Follow(following: Utente.Id, follower: Utente.Id)

  type Token = String

  case class Carta(id: Carta.Id, mazzo: Mazzo.Id)
  object Carta {
    type Id = Int
  }

  case class Ripetizione(carta: Carta.Id,
                         utente: Utente.Id,
                         step: Ripetizione.Step,
                         prossimaRipetizione: Date)
  object Ripetizione {
    type Step = Int
  }
}
