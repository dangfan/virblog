package dao

import com.google.inject.{Inject, Singleton}
import dao.OptionsImpl._
import dao.PGDriver.api._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}


@Singleton
private[dao] final
class OptionsImpl @Inject() (dbConfigProvider: DatabaseConfigProvider, implicit val ec: ExecutionContext)
  extends DAOLike(dbConfigProvider) with Options {

  def load(): Future[Unit] = {
    db.run(options.result).map(_.foreach {
      case ("blog_name", map) => Options.blogName = map
      case ("blog_description", map) => Options.blogDescription = map
      case ("locales", map) => Options.locales = map
      case ("datetime_format", map) => Options.datetimeFormat = map
      case ("default_locale", map) => Options.defaultLocale = map("value")
      case ("page_size", map) => Options.pageSize = map("value").toInt
      case ("disqus_short_name", map) => Options.disqusShortName = map("value")
      case ("ga_id", map) => Options.gaId = map("value")
    })
  }

  def setBlogName(map: Map[String, String]): Future[Int] = {
    Options.blogName = map
    db.run(options.filter(_.name === "blog_name").map(_.value).update(map))
  }

  def setBlogDescription(map: Map[String, String]): Future[Int] = {
    Options.blogDescription = map
    db.run(options.filter(_.name === "blog_description").map(_.value).update(map))
  }

  def setLocales(map: Map[String, String]): Future[Int] = {
    Options.locales = map
    db.run(options.filter(_.name === "locales").map(_.value).update(map))
  }

  def setDatetimeFormat(map: Map[String, String]): Future[Int] = {
    Options.datetimeFormat = map
    db.run(options.filter(_.name === "datetime_format").map(_.value).update(map))
  }

  def setDefaultLocale(value: String): Future[Int] = {
    Options.defaultLocale = value
    db.run(options.filter(_.name === "default_locale").map(_.value).update(Map("value" -> value)))
  }

  def setPageSize(value: Int): Future[Int] = {
    Options.pageSize = value
    db.run(options.filter(_.name === "page_size").map(_.value).update(Map("value" -> value.toString)))
  }

  def setDisqusShortName(value: String): Future[Int] = {
    Options.disqusShortName = value
    db.run(options.filter(_.name === "disqus_short_name").map(_.value).update(Map("value" -> value)))
  }

  def setGAId(value: String): Future[Int] = {
    Options.gaId = value
    db.run(options.filter(_.name === "ga_id").map(_.value).update(Map("value" -> value)))
  }

}

object OptionsImpl {

  private[dao]
  class Options(tag: Tag) extends Table[(String, Map[String, String])](tag, "OPTIONS") {
    def name = column[String]("NAME", O.PrimaryKey)
    def value = column[Map[String, String]]("VALUE")

    def * = (name, value)
  }

  private[dao] val options = TableQuery[Options]

}
