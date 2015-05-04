package models

import java.time._
import java.util.UUID
import shared.DAO
import shared.PGDriver.api._
import com.roundeights.hasher.Implicits._

import scala.concurrent._


case class User(username: String,
                password: String,
                sessionId: Option[String],
                expire: Option[LocalDateTime])

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def username = column[String]("USERNAME", O.PrimaryKey)
  def password = column[String]("PASSWORD")
  def sessionId = column[Option[String]]("SESSION_ID")
  def expire = column[Option[LocalDateTime]]("EXPIRE")

  def * = (username, password, sessionId, expire) <> (User.tupled, User.unapply)
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
    db.run(objects += User(username, saltedPassword, None, None))
  }

  def login(username: String, password: String)(implicit ec: ExecutionContext): Future[Option[String]] = {
    val saltedPassword = (salt + password).sha1.hex
    val q = objects.filter(_.username === username)
                   .filter(_.password === saltedPassword)
    db.run(q.result.headOption).map {
      case Some(user) =>
        val sessionId = UUID.randomUUID().toString
        val expire = LocalDateTime.now().plusDays(30)
        db.run(q.update(User(username, saltedPassword, Some(sessionId), Some(expire))))
        Some(sessionId)
      case _ => None
    }
  }

  def getUser(sessionId: String): Future[Option[User]] = {
    val q = objects.filter(_.sessionId === sessionId)
    db.run(q.result.headOption)
  }
}