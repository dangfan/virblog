package models

import java.time._

import models.enums.PostStatuses
import models.enums.PostTypes
import play.api.libs.functional.syntax._
import play.api.libs.json._
import shared.DAO
import shared.PGDriver.api._

import scala.concurrent._


case class Post(id: Option[Int],
                slug: String,
                time: LocalDateTime,
                title: Map[String, String],
                subtitle: Map[String, String],
                excerpt: Map[String, String],
                content: Map[String, String],
                headerImage: String,
                status: PostStatuses.PostStatus,
                postType: PostTypes.PostType,
                tags: List[String])

class Posts(tag: Tag) extends Table[Post](tag, "POSTS") {
  def id = column[Option[Int]]("ID", O.AutoInc, O.PrimaryKey)
  def slug = column[String]("SLUG")
  def time = column[LocalDateTime]("TIME")
  def title = column[Map[String, String]]("TITLE")
  def subtitle = column[Map[String, String]]("SUBTITLE")
  def excerpt = column[Map[String, String]]("EXCERPT")
  def content = column[Map[String, String]]("CONTENT")
  def headerImage = column[String]("HEADER_IMAGE")
  def status = column[PostStatuses.PostStatus]("POST_STATUS")
  def postType = column[PostTypes.PostType]("POST_TYPE")
  def tags = column[List[String]]("TAGS")

  def * = (id, slug, time, title, subtitle, excerpt, content, headerImage, status, postType, tags) <>
    (Post.tupled, Post.unapply)
}

object Posts {

  implicit val postReads: Reads[Post] = (
    (JsPath \ "id").readNullable[Int] and
    (JsPath \ "slug").read[String] and
    (JsPath \ "time").read[LocalDateTime] and
    (JsPath \ "title").read[Map[String, String]] and
    (JsPath \ "subtitle").read[Map[String, String]] and
    (JsPath \ "excerpt").read[Map[String, String]] and
    (JsPath \ "content").read[Map[String, String]] and
    (JsPath \ "headerImage").read[String] and
    (JsPath \ "status").read[PostStatuses.PostStatus] and
    (JsPath \ "postType").read[PostTypes.PostType] and
    (JsPath \ "tags").read[List[String]]
  )(Post.apply _)

  implicit val postWrites: Writes[Post] = (
    (JsPath \ "id").writeNullable[Int] and
    (JsPath \ "slug").write[String] and
    (JsPath \ "time").write[LocalDateTime] and
    (JsPath \ "title").write[Map[String, String]] and
    (JsPath \ "subtitle").write[Map[String, String]] and
    (JsPath \ "excerpt").write[Map[String, String]] and
    (JsPath \ "content").write[Map[String, String]] and
    (JsPath \ "headerImage").write[String] and
    (JsPath \ "status").write[PostStatuses.PostStatus] and
    (JsPath \ "postType").write[PostTypes.PostType] and
    (JsPath \ "tags").write[List[String]]
  )(unlift(Post.unapply))

  private val objects = TableQuery[Posts]
  private val db = DAO.db

  def count: Future[Int] = {
    db.run(objects.length.result)
  }

  def insert(obj: Post): Future[Int] = {
    db.run(objects += obj)
  }

  def delete(slug: String): Future[Int] = {
    db.run(objects.filter(_.slug === slug).delete)
  }

  def update(post: Post): Future[Int] = {
    db.run(objects.filter(_.id === post.id).update(post))
  }

  def getBySlug(slug: String): Future[Post] = {
    db.run(objects.filter(_.slug === slug).result.head)
  }

  def listByPage(page: Int, postType: PostTypes.PostType = PostTypes.Post, status: PostStatuses.PostStatus = PostStatuses.Published)(implicit ec: ExecutionContext): Future[(Seq[Post], Int)] = {
    val pageSize = Options.pageSize
    val q = objects.filter(_.postType === postType).filter(_.status === status).sortBy(_.time.desc)
    val filtered = q.drop(pageSize * (page - 1)).take(pageSize)
    for {
      posts <- db.run(filtered.result)
      len <- db.run(q.length.result)
    } yield (posts, len)
  }

  def listByTag(slug: String, page: Int)(implicit ec: ExecutionContext): Future[(Seq[Post], Int)] = {
    val pageSize = Options.pageSize
    val q = objects.filter(slug.bind === _.tags.any).filter(_.postType === PostTypes.Post).filter(_.status === PostStatuses.Published)
    val filtered = q.drop(pageSize * (page - 1)).take(pageSize)
    for {
      posts <- db.run(filtered.result)
      len <- db.run(q.length.result)
    } yield (posts, len)
  }
}