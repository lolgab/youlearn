package it.lorenzogabriele.impara.shared

case object TokenOverdueException extends Exception("Session token overdue")