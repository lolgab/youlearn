package it.lorenzogabriele.impara.shared.model

import fastparse.core.Parser
import fastparse.all._

case class DaCompletare(testo: String) extends Nota {
  private lazy val parsed: Option[List[Either[String, Cloze]]] = {
    val startC = P("{{c")
    val number = P(CharIn('0' to '9').rep(1).!.map(_.toInt))
    val sep = P("::")
    val endC = P("}}")
    val text = P((!(endC | startC | sep) ~ AnyChar).rep(1).!)
    val cloze = P(
      (startC ~ number ~ sep ~ text ~ (sep ~ text).? ~ endC).map(t =>
        Right(Cloze.tupled(t))))
    val outerText = P(
      (!(endC | startC | sep) ~ AnyChar).rep(1).!.map(Left.apply))
    val completeText = (cloze | outerText).rep ~ End
    val res = completeText.parse(testo)
    val resOpt = res match {
      case Parsed.Success(r, s) =>
        Some(r.toList) //TODO scegliere collezione giusta
      case f: Parsed.Failure => None
    }
    resOpt
  }

  lazy val clozes: List[Cloze] =
    parsed.getOrElse(Nil).filter(_.isRight).map(_.right.get)

  lazy val numeri: List[Int] = clozes.map(_.n).distinct

  lazy val isError: Boolean = parsed.isEmpty

  private def stringa(l: List[Either[String, Cloze]],
                      clozeStr: Cloze => String): String = l match {
    case Nil => ""
    case x :: xs =>
      val s = x match {
        case Left(str) => str
        case Right(cloze) =>
          clozeStr(cloze)
      }
      s + stringa(xs, clozeStr)
  }

  lazy val carte: List[Carta] = numeri.map(
    n =>
      Carta(
        DaCompletare.this.domanda(n),
        DaCompletare.this.risposta(n)
    ))

  def domanda(n: Int): String = {
    def clozeStr(cloze: Cloze) = {
      if (cloze.n == n) {
        val h = cloze.hint
        val str = if (h.isDefined) s"[${h.get}]" else "[...]"
        s"<font color='blue'>$str</font>" //TODO html server side is not general!!
      } else {
        cloze.hidden
      }

    }

    parsed match {
      case Some(l) => stringa(l, clozeStr)
      case None    => "ERRORE"
    }
  }

  def risposta(n: Int): String = {
    parsed match {
      case Some(l) => stringa(l, a => s"<font color='red'>${a.hidden}</font>") //TODO DIY
      case None    => "ERRORE"
    }
  }
}
