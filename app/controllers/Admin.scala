package controllers

import controllers.Actions._
import models.PostTags._
import models.Posts._
import models._
import models.enums._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class LoginInfo(username: String, password: String)

case class OptionInfo(blogName: Map[String, String],
                      blogDescription: Map[String, String],
                      locales: Map[String, String],
                      datetimeFormat: Map[String, String],
                      defaultLocale: String,
                      pageSize: Int,
                      disqusShortName: String)

object Admin extends Controller {
  implicit val loginInfoReads: Reads[LoginInfo] = (
    (JsPath \ "username").read[String] and
    (JsPath \ "password").read[String]
  )(LoginInfo.apply _)

  implicit val optionInfoReads: Reads[OptionInfo] = (
    (JsPath \ "blog_name").read[Map[String, String]] and
    (JsPath \ "blog_description").read[Map[String, String]] and
    (JsPath \ "locales").read[Map[String, String]] and
    (JsPath \ "datetime_format").read[Map[String, String]] and
    (JsPath \ "default_locale").read[String] and
    (JsPath \ "page_size").read[Int] and
    (JsPath \ "disqus_short_name").read[String]
  )(OptionInfo.apply _)

  implicit val optionInfoWrites: Writes[OptionInfo] = (
    (JsPath \ "blog_name").write[Map[String, String]] and
    (JsPath \ "blog_description").write[Map[String, String]] and
    (JsPath \ "locales").write[Map[String, String]] and
    (JsPath \ "datetime_format").write[Map[String, String]] and
    (JsPath \ "default_locale").write[String] and
    (JsPath \ "page_size").write[Int] and
    (JsPath \ "disqus_short_name").write[String]
  )(unlift(OptionInfo.unapply))

  val unauthorizedResult = Unauthorized(Json.obj(
    "status" -> "err",
    "message" -> "Invalid username and password"))

  val ok = Ok(Json.obj("status" -> "ok"))

  def login = Action.async(BodyParsers.parse.json) { request =>
    val loginInfoResult = request.body.validate[LoginInfo]
    loginInfoResult.fold(
      errors => Future(BadRequest(Json.obj("status" -> "err", "message" -> JsError.toJson(errors)))),
      loginInfo => Users.login(loginInfo.username, loginInfo.password).map {
        case Some(sessionId) => ok.withSession("sid" -> sessionId)
        case _ => unauthorizedResult
      }
    )
  }

  def logout = AuthenticatedAsyncAction { request =>
    Users.logout(request.user.username)
    Future(Redirect("/admin/"))
  }

  def getUserInfo = AuthenticatedAsyncAction { request =>
    val user = request.user
    Future(Ok(Json.obj("username" -> user.username, "email" -> user.email, "nickname" -> user.nickname)))
  }

  def tags = AuthenticatedAsyncAction { request =>
    PostTags.all.map { tags =>
      Ok(Json.toJson(tags))
    }
  }

  def addTag = AuthenticatedAsyncAction(BodyParsers.parse.json) { request =>
    val tagResult = request.body.validate[PostTag]
    tagResult.fold(
      errors => Future(BadRequest(Json.obj("status" -> "err", "message" -> JsError.toJson(errors)))),
      tag => PostTags.insert(tag).map(line => ok).recover {
        case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
      }
    )
  }

  def delTag(slug: String) = AuthenticatedAsyncAction { request =>
    PostTags.delete(slug).map(line => ok).recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }

  def updateTags = AuthenticatedAsyncAction(BodyParsers.parse.json) { request =>
    val tagResult = request.body.validate[List[PostTag]]
    tagResult.fold(
      errors => Future(BadRequest(Json.obj("status" -> "err", "message" -> JsError.toJson(errors)))),
      tags => PostTags.clear.flatMap { line =>
        Future.sequence(tags.map(PostTags.insert)).map { line => ok }.recover {
          case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
        }
      }.recover {
        case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
      }
    )
  }

  def posts(page: Int, postType: String, postStatus: String) = AuthenticatedAsyncAction { request =>
    for {
      (posts, postCount) <- Posts.listByPage(
        if (page < 0) 1 else page,
        PostTypes.withName(postType),
        PostStatuses.withName(postStatus)
      )
    } yield Ok(Json.obj("count" -> postCount, "data" -> posts))
  }

  def getPost(slug: String) = AuthenticatedAsyncAction { request =>
    val post = for {
      post <- Posts.getBySlug(slug)
    } yield Ok(Json.toJson(post))
    post.recover { case _ => NotFound(Json.obj("status" -> "err")) }
  }

  def addPost = AuthenticatedAsyncAction(BodyParsers.parse.json) { request =>
    val postResult = request.body.validate[Post]
    postResult.fold(
      errors => Future(BadRequest(Json.obj("status" -> "err", "message" -> JsError.toJson(errors)))),
      post => Posts.insert(post).map(line => ok).recover {
        case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
      }
    )
  }

  def delPost(slug: String) = AuthenticatedAsyncAction { request =>
    Posts.delete(slug).map(line => ok).recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }

  def updatePost = AuthenticatedAsyncAction(BodyParsers.parse.json) { request =>
    val postResult = request.body.validate[Post]
    postResult.fold(
      errors => Future(BadRequest(Json.obj("status" -> "err", "message" -> JsError.toJson(errors)))),
      post => Posts.update(post).map(line => ok).recover {
        case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
      }
    )
  }

  def getOptions = AuthenticatedAsyncAction { request =>
    val optionInfo = OptionInfo(Options.blogName, Options.blogDescription, Options.locales,
      Options.datetimeFormat, Options.defaultLocale, Options.pageSize, Options.disqusShortName)
    Future(Ok(Json.toJson(optionInfo)))
  }

  def updateOptions = AuthenticatedAsyncAction(BodyParsers.parse.json) { request =>
    val optionResult = request.body.validate[OptionInfo]
    optionResult.fold(
      errors => Future(BadRequest(Json.obj("status" -> "err", "message" -> JsError.toJson(errors)))),
      options => {
        Options.blogName = options.blogName
        Options.blogDescription = options.blogDescription
        Options.locales = options.locales
        Options.datetimeFormat = options.datetimeFormat
        Options.defaultLocale = options.defaultLocale
        Options.pageSize = options.pageSize
        Options.disqusShortName = options.disqusShortName
        Future(ok)
      }
    )
  }

  def updatePassword = AuthenticatedAsyncAction(BodyParsers.parse.json) { request =>
    val oldPassword = (request.body \ "old").as[String]
    val newPassword = (request.body \ "new").as[String]
    Users.updatePassword(request.user.username, newPassword, oldPassword).map { success =>
      if (success) ok else BadRequest(Json.obj("status" -> "err", "message" -> "Password not match"))
    }
  }

  def updateUser = AuthenticatedAsyncAction(BodyParsers.parse.json) { request =>
    val email = (request.body \ "email").as[String]
    val nickname = (request.body \ "nickname").as[String]
    Users.update(User(request.user.username, "", Some(email), Some(nickname), None, None)).map { _ =>
      ok
    }
  }
}