package models

import java.time._
import java.util.UUID
import shared.DAO
import shared.PGDriver.api._
import com.roundeights.hasher.Implicits._

import scala.concurrent._


case class User(username: String,
                password: String,
                email: Option[String],
                nickname: Option[String],
                sessionId: Option[String],
                expire: Option[LocalDateTime])

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def username = column[String]("USERNAME", O.PrimaryKey)
  def password = column[String]("PASSWORD")
  def email = column[Option[String]]("EMAIL")
  def nickname = column[Option[String]]("NICKNAME")
  def sessionId = column[Option[String]]("SESSION_ID")
  def expire = column[Option[LocalDateTime]]("EXPIRE")

  def * = (username, password, email, nickname, sessionId, expire) <> (User.tupled, User.unapply)
}

object Users {

  private val salt = "1cvI0NCpHL"
  private val objects = TableQuery[Users]
  private val db = DAO.db

  def count: Future[Int] = {
    db.run(objects.length.result)
  }

  def create(username: String, password: String): Future[Int] = {
    val saltedPassword = (salt + password).sha1.hex
    db.run(objects += User(username, saltedPassword, None, None, None, None))
  }

  def login(username: String, password: String)(implicit ec: ExecutionContext): Future[Option[String]] = {
    val saltedPassword = (salt + password).sha1.hex
    val q = objects.filter(_.username === username)
                   .filter(_.password === saltedPassword)
    db.run(q.result.headOption).map {
      case Some(_) =>
        val sessionId = UUID.randomUUID().toString
        val expire = LocalDateTime.now().plusDays(30)
        val update = q.map(u => (u.sessionId, u.expire)).update(Some(sessionId), Some(expire))
        db.run(update)
        Some(sessionId)
      case _ => None
    }
  }

  def getUser(sessionId: String): Future[Option[User]] = {
    val q = objects.filter(_.sessionId === sessionId)
    db.run(q.result.headOption)
  }

  def logout(username: String): Future[Int] = {
    val q = objects.filter(_.username === username).map(_.sessionId).update(None)
    db.run(q)
  }

  def updatePassword(username: String, newPassword: String, oldPassword: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    val saltedPassword = (salt + oldPassword).sha1.hex
    val q = objects.filter(_.username === username)
                   .filter(_.password === saltedPassword)
    db.run(q.result.headOption).map {
      case Some(_) =>
        val update = q.map(_.password).update((salt + newPassword).sha1.hex)
        db.run(update)
        true
      case _ => false
    }
  }

  def update(user: User): Future[Int] = {
    val q = objects.filter(_.username === user.username).map(u => (u.email, u.nickname))
                   .update(user.email, user.nickname)
    db.run(q)
  }
}