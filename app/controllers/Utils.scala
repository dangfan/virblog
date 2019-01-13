package controllers

import java.time._
import java.time.format._
import java.util.Locale

import com.sun.jna.Pointer
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import controllers.Implicits._
import dao.Options
import play.api.mvc.AnyContent
import sna.Library

import scala.collection.JavaConverters._


object Utils {
  private val mdOptions = new MutableDataSet().set(Parser.EXTENSIONS, Seq(StrikethroughExtension.create()).asJava)
  private val mdParser = Parser.builder(mdOptions).build()
  private val htmlRenderer = HtmlRenderer.builder(mdOptions).build()
  private val libopencc = Library("opencc")
  private val opencc = libopencc.opencc_open("s2t.json")[Pointer]

  def getNewLocalizedUrl(newLang: String)(implicit request: LocalizedRequest[AnyContent]): String = {
    val url = """/([\w-]+)/.*""".r
    request.uri match {
      case url(oldLang, _*) => request.uri.replace(oldLang, newLang)
      case _ => "/"
    }
  }

  def getMainLang(langCode: String): String = {
    langCode.split("-")(0)
  }

  def parseMarkdown(markdown: String): String = {
    val doc = mdParser.parse(markdown)
    htmlRenderer.render(doc)
  }

  def zhs2Zht(content: String): String = {
    val data = content.getBytes
    libopencc.opencc_convert_utf8(opencc, data, data.length)[String]
  }
}

final class LocalizedMap(val self: Map[String, String]) {
  def localize(implicit request: LocalizedRequest[AnyContent]): String = {
    self.getOrElse(request.lang, self.getOrElse(Options.defaultLocale, ""))
  }
}

final class LocalizedDatetime(val self: LocalDateTime) {
  def localize(implicit request: LocalizedRequest[AnyContent]): String = {
    self.format(DateTimeFormatter.ofPattern(Options.datetimeFormat.localize)
      .withLocale(new Locale(request.lang)))
  }
}