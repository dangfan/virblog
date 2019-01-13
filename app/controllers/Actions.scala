package controllers

import com.google.inject.Inject
import dao.Options
import models._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent._

class LocalizedRequest[A](val lang: String, request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedRequest[A](val user: UserEntity, request: Request[A]) extends WrappedRequest(request)

class LocalizedAction @Inject()(val parser: BodyParsers.Default)
                               (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[LocalizedRequest, AnyContent] {
  def invokeBlock[A](request: Request[A], block: LocalizedRequest[A] => Future[Result]): Future[Result] = {
    val url = """/([\w-]+)/.*""".r
    val lang = request.uri match {
      case url(l, _*) => l
      case _ => Options.defaultLocale
    }
    block(new LocalizedRequest(lang, request))
  }
}

class AuthenticatedAction @Inject()(val parser: BodyParsers.Default,
                                    userDao: dao.User)
                                   (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent] with Results {

  val unauthorizedResult: Result = Unauthorized(Json.obj(
    "status" -> "err",
    "message" -> "Invalid username and password"))

  def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    request.session.get("token") map { token =>
      userDao.getUser(token) flatMap {
        case Some(user) => block(new AuthenticatedRequest(user, request))
        case _ => Future.successful(unauthorizedResult)
      }
    } getOrElse Future.successful(unauthorizedResult)
  }
}