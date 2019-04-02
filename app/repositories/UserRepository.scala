package repositories

import javax.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def * = (id, firstName, lastName) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UserTable]

  def create(firstName: String, lastName: String): Future[User] = db.run {
    (users.map(u => (u.firstName, u.lastName))
      returning users.map(_.id)
      into ((data, id) => User(id, data._1, data._2))
      ) += (firstName, lastName)
  }

  def list(): Future[Seq[User]] = db.run {
    users.result
  }

  def getById(id: Long): Future[Option[User]] = db.run {
    users.filter{_.id === id}.result.headOption
  }
}
