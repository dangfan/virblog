package dao

import java.time.LocalDateTime

import com.google.inject.Inject
import dao.PGDriver.api._
import dao.PostImpl._
import models.enums.{PostStatuses, PostTypes}
import models.PostEntity
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}


private[dao] final
class PostImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
extends DAOLike(dbConfigProvider) with Post {

  def count: Future[Int] = db.run(posts.length.result)

  def insert(post: PostEntity): Future[Int] = db.run(posts += post)

  def delete(slug: String): Future[Int] = db.run(posts.filter(_.slug === slug).delete)

  def update(post: PostEntity): Future[Int] = db.run(posts.filter(_.id === post.id).update(post))

  def getBySlug(slug: String): Future[PostEntity] = db.run(posts.filter(_.slug === slug).result.head)

  def listByPage(page: Int, postType: PostTypes.PostType = PostTypes.Post, status: PostStatuses.PostStatus = PostStatuses.Published): Future[(Seq[PostEntity], Int)] = {
    val pageSize = Options.pageSize
    val q = posts.filter(_.postType === postType).filter(_.status === status).sortBy(_.time.desc)
    val filtered = q.drop(pageSize * (page - 1)).take(pageSize)
    for {
      posts <- db.run(filtered.result)
      len <- db.run(q.length.result)
    } yield (posts, len)
  }

  def listByTag(slug: String, page: Int): Future[(Seq[PostEntity], Int)] = {
    val pageSize = Options.pageSize
    val q = posts.filter(slug.bind === _.tags.any).filter(_.postType === PostTypes.Post).filter(_.status === PostStatuses.Published)
    val filtered = q.drop(pageSize * (page - 1)).take(pageSize)
    for {
      posts <- db.run(filtered.result)
      len <- db.run(q.length.result)
    } yield (posts, len)
  }
}

object PostImpl {
  private[dao]
  class Posts(tag: Tag) extends Table[PostEntity](tag, "POSTS") {
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
      (PostEntity.tupled, PostEntity.unapply)
  }

  private[dao] val posts = TableQuery[Posts]
}
