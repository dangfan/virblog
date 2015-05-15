package controllers

import java.time._
import java.time.format._
import java.util.Locale

import com.sun.jna.Pointer
import controllers.Implicits._
import models.Options
import org.pegdown.Extensions
import org.pegdown.PegDownProcessor
import play.api.i18n.Lang
import sna.Library


object Utils {
  private val pegdown = new PegDownProcessor(Extensions.ALL)
  private val libopencc = Library("opencc")
  private val opencc = libopencc.opencc_open("s2t.json")[Pointer]

  def getNewLocalizedUrl(newLang: String)(implicit request: LocalizedRequest): String = {
    val url = """/([\w-]+)/.*""".r
    request.uri match {
      case url(oldLang, _*) => request.uri.replace(oldLang, newLang)
      case _ => "/"
    }
  }

  def getLang(langCode: String): Lang = {
    // play does not support language with script, use tricks for specific languages
    if (langCode == "zh-Hans") return Lang("zhs")
    if (langCode == "zh-Hant") return Lang("zht")
    Lang(langCode)
  }

  def getMainLang(langCode: String): String = {
    langCode.split("-")(0)
  }

  def parseMarkdown(markdown: String): String = {
    pegdown.markdownToHtml(markdown)
  }

  def zhs2Zht(content: String): String = {
    val data = content.getBytes
    libopencc.opencc_convert_utf8(opencc, data, data.length)[String]
  }
}

final class LocalizedMap(val self: Map[String, String]) {
  def localize(implicit request: LocalizedRequest): String = {
    self.getOrElse(request.lang, self.getOrElse(Options.defaultLocale, ""))
  }
}

final class LocalizedDatetime(val self: LocalDateTime) {

  private def getLocale(code: String): Locale = {
    val lang = Utils.getLang(code)
    new Locale(lang.language)
  }

  def localize(implicit request: LocalizedRequest): String = {
    self.format(DateTimeFormatter.ofPattern(Options.datetimeFormat.localize)
      .withLocale(getLocale(request.lang)))
  }
}