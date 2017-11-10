package it.lorenzogabriele.impara.client.views

import io.udash._
import io.udash.bootstrap.button.UdashButton
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.RegistrazioneState
import it.lorenzogabriele.impara.shared.responses.{RegistrationCompleted, RegistrationResponse, UsernameAlreadyPresent}

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class RegistrazioneModel(username: String,
                              password: String,
                              confermaPassword: String,
                              nome: String,
                              cognome: String) //TODO Tipi per nome e cognome
object RegistrazioneModel {
  def empty = new RegistrazioneModel("", "", "", "", "")
}

case object RegistrazioneViewPresenter
    extends DefaultViewPresenterFactory[RegistrazioneState.type](() => {

      val minCaratteriPassword = 8

      val model: ModelProperty[RegistrazioneModel] =
        ModelProperty(RegistrazioneModel.empty)

      model.addValidator {
        model =>
          val errors = mutable.ArrayBuffer[String]()
          if (model.password.size < minCaratteriPassword)
            errors += s"La password deve contenere almeno $minCaratteriPassword caratteri."
          if (model.password.contains(""" """))
            errors += "La password non può contenere spazi."

          if (errors.isEmpty) Valid
          else Invalid(errors.map(DefaultValidationError))
      }

      model.addValidator { model =>
        if (model.password != model.confermaPassword)
          Invalid("Le due password inserite non corrispondono")
        else
          Valid
      }

      new RegistrazioneView(model)
    })

class RegistrazioneView(model: ModelProperty[RegistrazioneModel])
    extends View {

  import scalatags.JsDom.all._

  val messaggio = Property("")

  private val username = model.subProp(_.username)
  private val password = model.subProp(_.password)
  private val confermaPassword = model.subProp(_.confermaPassword)
  private val nome = model.subProp(_.nome)
  private val cognome = model.subProp(_.cognome)

  val buttonDisabled: Property[Boolean] = Property(false)

  val conferma = UdashButton(disabled = buttonDisabled)("Conferma")
  conferma.listen {
    case UdashButton.ButtonClickEvent(_) =>
      val response: Future[RegistrationResponse] =
        serverRpc.auth().registrazione(username.get, password.get, nome.get, cognome.get)
      response.onComplete {
        case Success(res) =>
          val m = res match {
            case RegistrationCompleted => {
              "Registrazione completata con successo."
            }
            case UsernameAlreadyPresent => "Username già presente!"
          }
          messaggio.set(m)
        case Failure(_) =>
          messaggio.set("C'è stato un errore nella registrazione, riprovare.")
      }
  }

  val tornaAllaHome = UdashButton()("Torna alla home")
  tornaAllaHome.listen {
    case UdashButton.ButtonClickEvent(_) =>
      applicationInstance.redirectTo("/")
  }

  private val content = div(
    h1("Registazione"),
    p("Username: "),
    TextInput.debounced(username)(placeholder := "Inserisci l'username..."),
    br,
    p("Password: "),
    PasswordInput.debounced(password)(
      placeholder := "Inserisci la password..."),
    valid(model) {
      case Valid => p().render
      case Invalid(errors) =>
        Seq(
          ul(errors.map(e => li(e.message)))
        ).map(_.render)
    },
    br,
    p("Conferma password: "),
    PasswordInput.debounced(confermaPassword)(
      placeholder := "Inserisci la password..."),
    br,
    p("Nome: "),
    TextInput.debounced(nome)(placeholder := "Inserisci il tuo nome..."),
    br,
    p("Cognome: "),
    TextInput.debounced(cognome)(placeholder := "Inserisci il tuo cognome..."),
    br,
    p(bind(messaggio)),
    conferma.render,
    br,
    tornaAllaHome.render
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {}

}
