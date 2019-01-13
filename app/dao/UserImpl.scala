package dao

import com.github.t3hnar.bcrypt._
import com.google.inject.Inject
import dao.PGDriver.api._
import dao.UserImpl._
import models.UserEntity
import pdi.jwt.{JwtAlgorithm, JwtJson}
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


private[dao] final
class UserImpl @Inject()(dbConfigProvider: DatabaseConfigProvider,
                         config: Configuration,
                         implicit val ec: ExecutionContext)
  extends DAOLike(dbConfigProvider) with User {

  private val jwtKey = config.get[String]("jwt.key")

  def count: Future[Int] = db.run(users.length.result)

  def create(username: String, password: String): Future[Int] = db.run(users += UserEntity(username, password.bcrypt, None, None))

  def login(username: String, password: String): Future[Option[String]] = {
    db.run(users.filter(_.username === username.trim).result.headOption) map {
      case Some(user) =>
        if (password.isBcrypted(user.password)) {
          Some(JwtJson.encode(Json.obj("name" -> user.username), jwtKey, JwtAlgorithm.HS256))
        } else {
          None
        }
      case _ => None
    }
  }

  def getUser(token: String): Future[Option[UserEntity]] = {
    JwtJson.decodeJson(token, jwtKey, Seq(JwtAlgorithm.HS256)) match {
      case Failure(_) => Future.successful(None)
      case Success(json) =>
        (json \ "name").asOpt[String] match {
          case None => Future.successful(None)
          case Some(username) => db.run(users.filter(_.username === username).result.headOption)
        }
    }
  }

  def updatePassword(username: String, newPassword: String, oldPassword: String): Future[Boolean] = {
    db.run(users.filter(_.username === username.trim).result.headOption) flatMap {
      case Some(user) =>
        if (oldPassword.isBcrypted(user.password)) {
          db.run(users.filter(_.username === username.trim).map(_.password).update(newPassword.bcrypt)) map (_ == 1)
        } else {
          Future.successful(false)
        }
      case _ => Future.successful(false)
    }
  }

  def update(user: UserEntity): Future[Boolean] = {
    db.run(users.filter(_.username === user.username).map(u => (u.email, u.nickname))
      .update(user.email, user.nickname)) map (_ == 1)
  }

}


object UserImpl {

  private[dao]
  class Users(tag: Tag) extends Table[UserEntity](tag, "USERS") {
    def username = column[String]("USERNAME", O.PrimaryKey)
    def password = column[String]("PASSWORD")
    def email = column[Option[String]]("EMAIL")
    def nickname = column[Option[String]]("NICKNAME")

    def * = (username, password, email, nickname) <> (UserEntity.tupled, UserEntity.unapply)
  }

  private[dao] val users = TableQuery[Users]

}

