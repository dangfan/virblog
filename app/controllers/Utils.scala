package controllers

import java.time._
import java.time.format._
import java.util.Locale

import controllers.Implicits._
import models.Options


object Utils {
  def getNewLocalizedUrl(newLang: String)(implicit request: LocalizedRequest): String = {
    val url = """/([\w-]+)/.*""".r
    request.request.path match {
      case url(oldLang, _*) => request.request.path.replace(oldLang, newLang)
      case _ => "/"
    }
  }
}

final class LocalizedMap(val self: Map[String, String]) {
  def localize(implicit request: LocalizedRequest): String = {
    self.getOrElse(request.lang, self.getOrElse(Options.defaultLocale, ""))
  }
}

final class LocalizedDatetime(val self: LocalDateTime) {
  private val SimpleLocale = """([a-zA-Z]{2,3})""".r
  private val CountryLocale = (SimpleLocale.toString + """-([a-zA-Z]{2}|[0-9]{3})""").r

  private def getLocale(code: String): Locale = {
    code match {
      case SimpleLocale(language) => new Locale(language)
      case CountryLocale(language, country) => new Locale(language, country)
      case _ => null
    }
  }

  def localize(implicit request: LocalizedRequest): String = {
    self.format(DateTimeFormatter.ofPattern(Options.datetimeFormat.localize)
      .withLocale(getLocale(request.lang)))
  }
}