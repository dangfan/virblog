package dao

import com.google.inject.ImplementedBy
import models.BlogrollEntity

import scala.concurrent.Future


@ImplementedBy(classOf[BlogrollImpl])
trait Blogroll {

  def load(): Future[Unit]

  def reset(blogrolls: Seq[BlogrollEntity]): Future[Unit]

}

object Blogroll {
  var all: Seq[BlogrollEntity] = Seq()
}
