package models

import models.PostStatus.PostStatus
import play.api.libs.json._
import shared.DAO
import shared.PGDriver.api._

import scala.concurrent.Future


case class Post(slug: String,
                categoryId: Long,
                timestamp: Long,
                status: PostStatus,
                content: JsValue)

class Posts(tag: Tag) extends Table[Post](tag, "POSTS") {
  def slug = column[String]("SLUG", O.PrimaryKey)
  def categoryId = column[Long]("CATEGORY_ID")
  def timestamp = column[Long]("TIMESTAMP")
  def status = column[PostStatus]("POST_STATUS")
  def content = column[JsValue]("CONTENT")

  def * = (slug, categoryId, timestamp, status, content) <>(Post.tupled, Post.unapply)

  def category = foreignKey("CATEGORY_FK", categoryId, TableQuery[Categories])(_.id)
}

object Posts {
  private val posts = TableQuery[Posts]

  val db = DAO.db

  def count: Future[Int] = db.run(posts.length.result)

  def insert(post: Post): Future[Int] = db.run(posts += post)
}