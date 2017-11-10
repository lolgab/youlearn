package it.lorenzogabriele.impara.client.views.studiaMazzo

import it.lorenzogabriele.impara.shared.model.db.tables.Mazzo
import it.lorenzogabriele.impara.shared.model.Carta

case class StudiaMazzoModel(mazzo: Mazzo, ripetizioniTerminate: Boolean = false,carta: Carta, idCarta: Int, mostraRisposta: Boolean = false) //TODO Int brutto
