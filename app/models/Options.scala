package models

import shared.DAO
import shared.PGDriver.api._

import scala.concurrent.ExecutionContext


class Options(tag: Tag) extends Table[(String, Map[String, String])](tag, "OPTIONS") {
  def name = column[String]("NAME", O.PrimaryKey)
  def value = column[Map[String, String]]("VALUE")

  def * = (name, value)
}

object Options {
  private val options = TableQuery[Options]
  private val db = DAO.db

  private var privateBlogName: Map[String, String] = Map()
  private var privateBlogDescription: Map[String, String] = Map()
  private var privateLocales: Map[String, String] = Map()
  private var privateDatetimeFormat: Map[String, String] = Map()
  private var privateDefaultLocale: String = ""
  private var privatePageSize: Int = 10
  private var privateDisqusShortName: String = ""
  private var privateCnzzId = ""
  private var privateGoogleAnalyticsId = ""


  def load()(implicit ec: ExecutionContext) = {
    db.run(options.result).map(_.foreach {
      case ("blog_name", map) => privateBlogName = map
      case ("blog_description", map) => privateBlogDescription = map
      case ("locales", map) => privateLocales = map
      case ("datetime_format", map) => privateDatetimeFormat = map
      case ("default_locale", map) => privateDefaultLocale = map("value")
      case ("page_size", map) => privatePageSize = map("value").toInt
      case ("disqus_short_name", map) => privateDisqusShortName = map("value")
      case ("cnzz_id", map) => privateCnzzId = map("value")
      case ("ga_id", map) => privateGoogleAnalyticsId = map("value")
    })
  }

  def blogName = privateBlogName
  def blogDescription = privateBlogDescription
  def locales = privateLocales
  def datetimeFormat = privateDatetimeFormat
  def defaultLocale = privateDefaultLocale
  def pageSize = privatePageSize
  def disqusShortName = privateDisqusShortName
  def cnzzId = privateCnzzId
  def gaId = privateGoogleAnalyticsId

  def blogName_=(map: Map[String, String]) = {
    privateBlogName = map
    db.run(options.filter(_.name === "blog_name").map(_.value).update(map))
  }

  def blogDescription_=(map: Map[String, String]) = {
    privateBlogDescription = map
    db.run(options.filter(_.name === "blog_description").map(_.value).update(map))
  }

  def locales_=(map: Map[String, String]) = {
    privateLocales = map
    db.run(options.filter(_.name === "locales").map(_.value).update(map))
  }

  def datetimeFormat_=(map: Map[String, String]) = {
    privateDatetimeFormat = map
    db.run(options.filter(_.name === "datetime_format").map(_.value).update(map))
  }

  def defaultLocale_=(value: String) = {
    privateDefaultLocale = value
    db.run(options.filter(_.name === "default_locale").map(_.value).update(Map("value" -> value)))
  }

  def pageSize_=(value: Int) = {
    privatePageSize = value
    db.run(options.filter(_.name === "page_size").map(_.value).update(Map("value" -> value.toString)))
  }

  def disqusShortName_=(value: String) = {
    privateDisqusShortName = value
    db.run(options.filter(_.name === "disqus_short_name").map(_.value).update(Map("value" -> value)))
  }

  def cnzzId_=(value: String) = {
    privateCnzzId = value
    db.run(options.filter(_.name === "cnzz_id").map(_.value).update(Map("value" -> value)))
  }

  def gaId_=(value: String) = {
    privateGoogleAnalyticsId = value
    db.run(options.filter(_.name === "ga_id").map(_.value).update(Map("value" -> value)))
  }

}