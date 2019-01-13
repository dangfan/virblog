package dao

import com.google.inject.ImplementedBy
import models.UserEntity

import scala.concurrent.Future


@ImplementedBy(classOf[UserImpl])
trait User {

  def count: Future[Int]

  def create(username: String, password: String): Future[Int]

  def login(username: String, password: String): Future[Option[String]]

  def getUser(token: String): Future[Option[UserEntity]]

  def updatePassword(username: String, newPassword: String, oldPassword: String): Future[Boolean]

  def update(user: UserEntity): Future[Boolean]

}
