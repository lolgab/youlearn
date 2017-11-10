package it.lorenzogabriele.impara.client.views

import io.udash._
import io.udash.bootstrap.button.UdashButton
import it.lorenzogabriele.impara.client.Context._
import it.lorenzogabriele.impara.client.{IndexState, LoginState, UserService}
import it.lorenzogabriele.impara.shared.responses.{LoginCompleted, LoginResponse, UsernameNotPresent, WrongPassword}

import scala.concurrent.Future
import scala.util.{Failure, Success}

case class LoginModel(username: String, password: String)
object LoginModel {
  def empty = new LoginModel("", "")
}

case object LoginViewPresenter
    extends DefaultViewPresenterFactory[LoginState.type](() => {
      val model: ModelProperty[LoginModel] = ModelProperty(LoginModel.empty)

      new LoginView(model)
    })

class LoginView(model: ModelProperty[LoginModel]) extends View {

  import scalatags.JsDom.all._

  val messaggio = Property("")

  private val username = model.subProp(_.username)
  private val password = model.subProp(_.password)

  val conferma = UdashButton()("Conferma")
  conferma.listen {
    case UdashButton.ButtonClickEvent(_) =>
      val actualUser = username.get
      val actualPass = password.get
      val response: Future[LoginResponse] =
        UserService.login(username.get, actualPass)
      response.onComplete {
        case Success(res) =>
          val m = res match {
            case LoginCompleted(token) =>
              "Login completato con successo."
            case UsernameNotPresent =>
              s"L'username $actualUser non è registrato!"
            case WrongPassword =>
              s"Password sbagliata per l'utente $actualUser"
          }
          messaggio.set(m)
        case Failure(_) =>
          messaggio.set("C'è stato un errore nell'accesso, riprovare.")
      }
  }

  val tornaAllaHome = UdashButton()("Torna alla home")
  tornaAllaHome.listen {
    case UdashButton.ButtonClickEvent(_) =>
      applicationInstance.goTo(IndexState)
  }

  private val content = div(
    h1("Login"),
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
    p(bind(messaggio)),
    conferma.render,
    br,
    tornaAllaHome.render
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {}
}
