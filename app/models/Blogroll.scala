package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import shared.DAO
import shared.PGDriver.api._

import scala.concurrent.{ExecutionContext, Future}

case class Blogroll(name: String, link: String)

class Blogrolls(tag: Tag) extends Table[Blogroll](tag, "BLOGROLLS") {
  def name = column[String]("NAME")
  def link = column[String]("LINK")

  def * = (name, link) <>(Blogroll.tupled, Blogroll.unapply)
}

object Blogrolls {
  implicit val itemWrites: Writes[Blogroll] = (
    (JsPath \ "name").write[String] and
    (JsPath \ "link").write[String]
  )(unlift(Blogroll.unapply))

  implicit val itemReads: Reads[Blogroll] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "link").read[String]
  )(Blogroll.apply _)

  private val objects = TableQuery[Blogrolls]
  private val db = DAO.db
  private var privateAll: Seq[Blogroll] = Seq[Blogroll]()

  def insertAll(objs: Seq[Blogroll]) = {
    privateAll = objs
    db.run(objects ++= objs)
  }

  def clear: Future[Int] = {
    db.run(objects.delete)
  }

  def all = privateAll

  def load()(implicit ec: ExecutionContext) = {
    db.run(objects.result).map { seq =>
      privateAll = seq
    }
  }

}