package models

import play.api.libs.json._
import shared.DAO
import shared.PGDriver.api._

import scala.concurrent.Future

case class Category(id: Option[Long], name: JsValue)

class Categories(tag: Tag) extends Table[Category](tag, "CATEGORIES") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[JsValue]("NAME")

  def * = (id.?, name) <>(Category.tupled, Category.unapply)
}

object Categories {
  private val categories = TableQuery[Categories]

  val db = DAO.db

  def count: Future[Int] = db.run(categories.length.result)

  def insert(category: Category): Future[Int] = db.run(categories += category)

  def all: Future[Seq[Category]] = db.run(categories.result)
}