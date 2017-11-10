/*
class MockDb extends Db {
  var users     = List[String]()
  var passwords = Map[String, String]()
  var mazzi     = List[Mazzo]()

  def addUser(login: String, password: String): Unit =
    Future {
      users = login :: users
      passwords += login -> password
    }

  def removeUser(login: String): Unit = Future {
    users.filterNot(_ == login)
    passwords.filterKeys(_ == login)
  }

  def passwordOf(user: String): Future[Option[String]] = Future {
    passwords.get(user)
  }

  def registeredUsers: Future[Seq[String]] = Future { users }

  def userIsRegistered(login: String): Future[Boolean] = Future {
    users.contains(login)
  }

  def addMazzo(nome: String, creator: String): Unit =
    Future {
      mazzi ::= Mazzo(nome, Seq(), Seq())
    }
}
 */
