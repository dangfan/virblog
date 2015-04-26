package controllers

import play.api.Play._
import play.api.i18n.Messages.Implicits._
import play.api.i18n._
import play.api.mvc._

import scala.concurrent.Future

case class LocalizedRequest(lang: String,
                            request: Request[AnyContent])
  extends WrappedRequest(request)


object Actions {

  def LocalizedAction(lang: String)(f: LocalizedRequest => Result) = {
    Action { request =>
      f(LocalizedRequest(lang, request))
    }
  }

  def LocalizedAsyncAction(lang: String)(f: LocalizedRequest => Future[Result]) = {
    Action.async { request =>
      f(LocalizedRequest(lang, request))
    }
  }

  object Implicits {
    implicit def localizedRequest2Messages(implicit request: LocalizedRequest): Messages = applicationMessages(Lang(request.lang), current)
  }

}
