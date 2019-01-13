package dao

import com.google.inject.ImplementedBy
import models.PostTagEntity

import scala.concurrent.Future


@ImplementedBy(classOf[PostTagImpl])
trait PostTag {

  def count: Future[Int]

  def insert(obj: PostTagEntity): Future[Int]

  def delete(slug: String): Future[Int]

  def clear: Future[Int]

  def update(tag: PostTagEntity): Future[Int]

  def all: Future[Seq[PostTagEntity]]

  def getBySlug(slug: String): Future[PostTagEntity]

  def getBySlugs(slugs: List[String]): Future[Seq[PostTagEntity]]

}
