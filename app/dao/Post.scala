package dao

import com.google.inject.ImplementedBy
import models.PostEntity
import models.enums.{PostStatuses, PostTypes}

import scala.concurrent.Future


@ImplementedBy(classOf[PostImpl])
trait Post {
  def count: Future[Int]

  def insert(obj: PostEntity): Future[Int]

  def delete(slug: String): Future[Int]

  def update(post: PostEntity): Future[Int]

  def getBySlug(slug: String): Future[PostEntity]

  def listByPage(page: Int, postType: PostTypes.PostType = PostTypes.Post, status: PostStatuses.PostStatus = PostStatuses.Published): Future[(Seq[PostEntity], Int)]

  def listByTag(slug: String, page: Int): Future[(Seq[PostEntity], Int)]
}
