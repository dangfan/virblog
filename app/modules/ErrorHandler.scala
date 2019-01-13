package modules

import com.google.inject.{Inject, Singleton}
import controllers.LocalizedRequest
import dao.Options
import play.api.http.HttpErrorHandler
import play.api.i18n.MessagesApi
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent._


@Singleton
class ErrorHandler @Inject()(messagesApi: MessagesApi) extends HttpErrorHandler {
  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    if (statusCode == play.api.http.Status.NOT_FOUND) {
      val url = """(zh-Hans|zh-Hant|en)""".r
      val lang = url.findFirstIn(request.path).getOrElse(Options.defaultLocale)
      val localizedRequest = new LocalizedRequest(lang, Request[AnyContent](request, AnyContentAsEmpty))
      Future.successful(NotFound(views.html.notFound()(localizedRequest, messagesApi)))
    } else {
      Future.successful(
        Status(statusCode)("A client error occurred: " + message)
      )
    }
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}
