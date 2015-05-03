package models

import models.enums.PostStatus.PostStatus
import models.enums.PostType.PostType
import shared.DAO
import shared.PGDriver.api._

import scala.concurrent.Future
import java.time._


case class Post(slug: String,
                time: LocalDateTime,
                title: Map[String, String],
                subtitle: Map[String, String],
                excerpt: Map[String, String],
                content: Map[String, String],
                headerImage: Option[String],
                status: PostStatus,
                postType: PostType,
                tags: List[String])

class Posts(tag: Tag) extends Table[Post](tag, "POSTS") {
  def slug = column[String]("SLUG", O.PrimaryKey)
  def time = column[LocalDateTime]("TIME")
  def title = column[Map[String, String]]("TITLE")
  def subtitle = column[Map[String, String]]("SUBTITLE")
  def excerpt = column[Map[String, String]]("EXCERPT")
  def content = column[Map[String, String]]("CONTENT")
  def headerImage = column[Option[String]]("HEADER_IMAGE")
  def status = column[PostStatus]("POST_STATUS")
  def postType = column[PostType]("POST_TYPE")
  def tags = column[List[String]]("TAGS")

  def * = (slug, time, title, subtitle, excerpt, content, headerImage, status, postType, tags) <>
    (Post.tupled, Post.unapply)
}

object Posts {
  private val objects = TableQuery[Posts]
  private val db = DAO.db

  def count: Future[Int] = db.run(objects.length.result)
  def insert(obj: Post): Future[Int] = db.run(objects += obj)
  def listByPage(page: Int, pageSize: Int = Options.pageSize): Future[Seq[Post]] = db.run(objects.drop(pageSize * (page - 1)).take(pageSize).result)
  def getBySlug(slug: String): Future[Post] = db.run(objects.filter(_.slug === slug).result.head)
}