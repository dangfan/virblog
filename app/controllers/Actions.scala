package controllers

import models._
import play.api.mvc._

import scala.concurrent._

case class LocalizedRequest(lang: String, country: String, request: Request[AnyContent])
  extends WrappedRequest(request)

case class AuthenticatedRequest[A](user: User, request: Request[A])
  extends WrappedRequest(request)


object Actions {

  def LocalizedAsyncAction(lang: String)(f: LocalizedRequest => Future[Result])(implicit ec: ExecutionContext) = {
    Action.async { request =>
      val ip = request.headers.get("X-Real-IP").getOrElse(request.remoteAddress)
      IpUtils.getCountry(ip).flatMap { country =>
        f(LocalizedRequest(lang, country, request))
      }
    }
  }

  def AuthenticatedAsyncAction[A](bodyParser: play.api.mvc.BodyParser[A])(f: AuthenticatedRequest[A] => Future[Result])(implicit ec: ExecutionContext) = {
    Action.async(bodyParser) { request =>
      request.session.get("sid").map { sid =>
        Users.getUser(sid).flatMap {
          case Some(user) => f(AuthenticatedRequest(user, request))
          case _ => Future(Admin.unauthorizedResult)
        }
      }.getOrElse(Future(Admin.unauthorizedResult))
    }
  }

  def AuthenticatedAsyncAction(f: AuthenticatedRequest[AnyContent] => Future[Result])(implicit ec: ExecutionContext): Action[AnyContent] = {
    AuthenticatedAsyncAction(BodyParsers.parse.default)(f)
  }

}
