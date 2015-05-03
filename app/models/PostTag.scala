package models

import shared.DAO
import shared.PGDriver.api._

import scala.concurrent.Future

case class PostTag(slug: String, name: Map[String, String])

class PostTags(tag: Tag) extends Table[PostTag](tag, "POST_TAGS") {
  def slug = column[String]("SLUG", O.PrimaryKey)
  def name = column[Map[String, String]]("NAME")

  def * = (slug, name) <> (PostTag.tupled, PostTag.unapply)
}

object PostTags {

  private val objects = TableQuery[PostTags]
  private val db = DAO.db

  def count: Future[Int] = db.run(objects.length.result)
  def insert(obj: PostTag): Future[Int] = db.run(objects += obj)
  def all: Future[Seq[PostTag]] = db.run(objects.result)
  def getBySlugs(slugs: List[String]): Future[Seq[PostTag]] = db.run(objects.filter(_.slug inSet slugs).result)
}