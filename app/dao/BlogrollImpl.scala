package dao

import com.google.inject.Inject
import dao.BlogrollImpl._
import dao.PGDriver.api._
import models.BlogrollEntity
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}


private[dao] final
class BlogrollImpl @Inject() (dbConfigProvider: DatabaseConfigProvider, implicit val ec: ExecutionContext)
  extends DAOLike(dbConfigProvider) with Blogroll {

  def load(): Future[Unit] = db.run(blogrolls.result) map (Blogroll.all = _)

  def reset(blogrollEntities: Seq[BlogrollEntity]): Future[Unit] = {
    Blogroll.all = blogrollEntities
    for {
      _ <- db.run(blogrolls.delete)
      _ <- db.run(blogrolls ++= blogrollEntities)
    } yield ()
  }

}

object BlogrollImpl {

  private[dao]
  class Blogrolls(tag: Tag) extends Table[BlogrollEntity](tag, "BLOGROLLS") {
    def name = column[String]("NAME")
    def link = column[String]("LINK")

    def * = (name, link) <> (BlogrollEntity.tupled, BlogrollEntity.unapply)
  }

  private[dao] val blogrolls = TableQuery[Blogrolls]

}
