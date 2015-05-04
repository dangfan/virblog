package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import shared.DAO
import shared.PGDriver.api._

import scala.concurrent.Future

case class PostTag(slug: String, name: Map[String, String])

class PostTags(tag: Tag) extends Table[PostTag](tag, "POST_TAGS") {
  def slug = column[String]("SLUG", O.PrimaryKey)
  def name = column[Map[String, String]]("NAME")

  def * = (slug, name) <>(PostTag.tupled, PostTag.unapply)
}

object PostTags {
  implicit val tagWrites: Writes[PostTag] = (
    (JsPath \ "slug").write[String] and
    (JsPath \ "name").write[Map[String, String]]
  )(unlift(PostTag.unapply))

  implicit val tagReads: Reads[PostTag] = (
    (JsPath \ "slug").read[String] and
    (JsPath \ "name").read[Map[String, String]]
  )(PostTag.apply _)

  private val objects = TableQuery[PostTags]
  private val db = DAO.db

  def count: Future[Int] = {
    db.run(objects.length.result)
  }

  def insert(obj: PostTag): Future[Int] = {
    db.run(objects += obj)
  }

  def delete(slug: String): Future[Int] = {
    db.run(objects.filter(_.slug === slug).delete)
  }

  def update(tag: PostTag): Future[Int] = {
    db.run(objects.filter(_.slug === tag.slug).map(_.name).update(tag.name))
  }

  def all: Future[Seq[PostTag]] = {
    db.run(objects.result)
  }

  def getBySlug(slug: String): Future[PostTag] = {
    db.run(objects.filter(_.slug === slug).result.head)
  }

  def getBySlugs(slugs: List[String]): Future[Seq[PostTag]] = {
    db.run(objects.filter(_.slug inSet slugs).result)
  }
}