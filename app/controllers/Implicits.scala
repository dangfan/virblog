package controllers

import java.time._

import play.api.i18n._
import play.api.mvc.AnyContent

import scala.language.implicitConversions


object Implicits {
  implicit def localizedRequest2MessagesProvider(implicit request: LocalizedRequest[AnyContent],
                                                 messagesApi: MessagesApi): MessagesProvider = {
    MessagesImpl(Lang(request.lang), messagesApi)
  }

  implicit def localizedMapWrapper(m: Map[String, String]): LocalizedMap = new LocalizedMap(m)

  implicit def localizedTimeWrapper(time: LocalDateTime): LocalizedDatetime = new LocalizedDatetime(time)
}
