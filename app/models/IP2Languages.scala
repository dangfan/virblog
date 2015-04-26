package models

import java.net._

import shared.DAO
import shared.PGDriver.api._

import scala.concurrent._

class IP2Countries(tag: Tag) extends Table[(BigDecimal, String)](tag, "IP2COUNTRIES") {
  def ip = column[BigDecimal]("IP")
  def country = column[String]("COUNTRY")

  def * = (ip, country)
}

class Country2Languages(tag: Tag) extends Table[(String, String, String)](tag, "COUNTRY2LANGUAGES") {
  def code = column[String]("CODE")
  def lang_code = column[String]("LANG_CODE")
  def iso_country = column[String]("ISO_COUNTRY")

  def * = (code, lang_code, iso_country)
}

object IP2Languages {
  private val ip2Countries = TableQuery[IP2Countries]
  private val country2Languages = TableQuery[Country2Languages]
  val db = DAO.db

  def ipToDecimal(ip: String): BigDecimal = {
    val ia = InetAddress.getByName(ip)
    BigDecimal(BigInt(1, ia.getAddress))
  }

  def getLangCode(ip: String): Future[String] = {
    val q = (for {
      i <- ip2Countries.sortBy(_.ip) if i.ip >= ipToDecimal(ip)
      c <- country2Languages if i.country === c.code
    } yield c.lang_code).take(1)
    db.run(q.result.head)
  }
}