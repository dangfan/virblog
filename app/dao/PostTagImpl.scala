package dao

import com.google.inject.Inject
import dao.PGDriver.api._
import dao.PostTagImpl._
import models.PostTagEntity
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future


private[dao] final
class PostTagImpl @Inject() (dbConfigProvider: DatabaseConfigProvider, options: Options)
  extends DAOLike(dbConfigProvider) with PostTag {

  def count: Future[Int] = db.run(postTags.length.result)

  def insert(tag: PostTagEntity): Future[Int] = db.run(postTags += tag)

  def delete(slug: String): Future[Int] = db.run(postTags.filter(_.slug === slug).delete)

  def clear: Future[Int] = db.run(postTags.delete)

  def update(tag: PostTagEntity): Future[Int] = db.run(postTags.filter(_.slug === tag.slug).map(_.name).update(tag.name))

  def all: Future[Seq[PostTagEntity]] = db.run(postTags.result)

  def getBySlug(slug: String): Future[PostTagEntity] = db.run(postTags.filter(_.slug === slug).result.head)

  def getBySlugs(slugs: List[String]): Future[Seq[PostTagEntity]] = db.run(postTags.filter(_.slug inSet slugs).result)
}

object PostTagImpl {

  private[dao]
  class PostTags(tag: Tag) extends Table[PostTagEntity](tag, "POST_TAGS") {
    def slug = column[String]("SLUG", O.PrimaryKey)
    def name = column[Map[String, String]]("NAME")

    def * = (slug, name) <> (PostTagEntity.tupled, PostTagEntity.unapply)
  }

  private[dao] val postTags = TableQuery[PostTags]

}
