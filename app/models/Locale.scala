package models

import shared.DAO
import shared.PGDriver.api._

import scala.concurrent.Future

case class Locale(id: String, name: String)

class Locales(tag: Tag) extends Table[Locale](tag, "LOCALES") {
  def id = column[String]("ID", O.PrimaryKey)

  def name = column[String]("NAME")

  def * = (id, name) <>(Locale.tupled, Locale.unapply)
}

object Locales {
  private val locales = TableQuery[Locales]

  val db = DAO.db

  def count: Future[Int] = db.run(locales.length.result)

  def insert(locale: Locale): Future[Int] = db.run(locales += locale)
}