package dao

import com.google.inject.ImplementedBy

import scala.concurrent.Future


@ImplementedBy(classOf[OptionsImpl])
trait Options {

  def load(): Future[Unit]

  def setBlogName(map: Map[String, String]): Future[Int]

  def setBlogDescription(map: Map[String, String]): Future[Int]

  def setLocales(map: Map[String, String]): Future[Int]

  def setDatetimeFormat(map: Map[String, String]): Future[Int]

  def setDefaultLocale(value: String): Future[Int]

  def setPageSize(value: Int): Future[Int]

  def setDisqusShortName(value: String): Future[Int]

  def setGAId(value: String): Future[Int]

}

object Options {
  var blogName: Map[String, String] = Map()
  var blogDescription: Map[String, String] = Map()
  var locales: Map[String, String] = Map()
  var datetimeFormat: Map[String, String] = Map()
  var defaultLocale: String = ""
  var pageSize: Int = 10
  var disqusShortName: String = ""
  var gaId: String = ""
}
