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

  private var privateBlogName: Map[String, String] = null
  private var privateBlogDescription: Map[String, String] = null
  private var privateLocales: Map[String, String] = null
  private var privateDatetimeFormat: Map[String, String] = null
  private var privateDefaultLocale: String = null
  private var privatePageSize: Int = 10


  def load()(implicit ec: ExecutionContext) = {
    db.run(options.result).map(_.foreach {
      case ("blog_name", map) => privateBlogName = map
      case ("blog_description", map) => privateBlogDescription = map
      case ("locales", map) => privateLocales = map
      case ("datetime_format", map) => privateDatetimeFormat = map
      case ("default_locale", map) => privateDefaultLocale = map("value")
      case ("page_size", map) => privatePageSize = map("value").toInt
    })
  }

  def blogName = privateBlogName
  def blogDescription = privateBlogDescription
  def locales = privateLocales
  def datetimeFormat = privateDatetimeFormat
  def defaultLocale = privateDefaultLocale
  def pageSize = privatePageSize

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
}