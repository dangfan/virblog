package controllers

import com.google.inject.Inject
import controllers.Admin._
import dao.{Blogroll, Options}
import models._
import models.enums._
import play.api.i18n.Langs
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


case class LoginInfo(username: String, password: String)

case class OptionInfo(blogName: Map[String, String],
                      blogDescription: Map[String, String],
                      locales: Map[String, String],
                      datetimeFormat: Map[String, String],
                      defaultLocale: String,
                      pageSize: Int,
                      disqusShortName: String,
                      gaId: String)

class Admin @Inject()(postDao: dao.Post,
                      postTagDao: dao.PostTag,
                      userDao: dao.User,
                      optionsDao: dao.Options,
                      blogrollDao: dao.Blogroll,
                      authenticatedAction: AuthenticatedAction,
                      langs: Langs,
                      val controllerComponents: ControllerComponents)
                     (implicit val ec: ExecutionContext) extends BaseController {


  val unauthorizedResult: Result = Unauthorized(Json.obj(
    "status" -> "err",
    "message" -> "Invalid username and password"))

  val ok: Result = Ok(Json.obj("status" -> "ok"))

  def login: Action[LoginInfo] = Action.async(parse.json[LoginInfo]) { request =>
    val loginInfo = request.body
    userDao.login(loginInfo.username, loginInfo.password).map {
      case Some(sessionId) => ok.withSession("sid" -> sessionId)
      case _ => unauthorizedResult
    }
  }

  def logout: Action[AnyContent] = authenticatedAction.async { _ =>
    Future(Redirect("/admin/").withNewSession)
  }

  def getUserInfo: Action[AnyContent] = authenticatedAction.async { request =>
    val user = request.user
    Future(Ok(Json.obj("username" -> user.username, "email" -> user.email, "nickname" -> user.nickname)))
  }

  def tags: Action[AnyContent] = authenticatedAction.async { _ =>
    postTagDao.all.map { tags =>
      Ok(Json.toJson(tags))
    }
  }

  def addTag: Action[PostTagEntity] = authenticatedAction.async(parse.json[PostTagEntity]) { request =>
    postTagDao.insert(request.body).map(_ => ok).recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }

  def delTag(slug: String): Action[AnyContent] = authenticatedAction.async { _ =>
    postTagDao.delete(slug).map(_ => ok).recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }

  def updateTags: Action[Seq[PostTagEntity]] = authenticatedAction.async(parse.json[Seq[PostTagEntity]]) { request =>
    val tags = request.body
    postTagDao.clear.flatMap { _ =>
      Future.sequence(tags.map(postTagDao.insert)).map { _ => ok }.recover {
        case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
      }
    }.recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }

  def posts(page: Int, postType: String, postStatus: String): Action[AnyContent] = authenticatedAction.async { _ =>
    for {
      (posts, postCount) <- postDao.listByPage(
        if (page < 0) 1 else page,
        PostTypes.withName(postType),
        PostStatuses.withName(postStatus)
      )
    } yield Ok(Json.obj("count" -> postCount, "data" -> posts))
  }

  def getPost(slug: String): Action[AnyContent] = authenticatedAction.async { _ =>
    val post = for {
      post <- postDao.getBySlug(slug)
    } yield Ok(Json.toJson(post))
    post.recover { case _ => NotFound(Json.obj("status" -> "err")) }
  }

  def addPost: Action[PostEntity] = authenticatedAction.async(parse.json[PostEntity]) { request =>
    postDao.insert(request.body).map(_ => ok).recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }

  def delPost(slug: String): Action[AnyContent] = authenticatedAction.async { _ =>
    postDao.delete(slug).map(_ => ok).recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }

  def updatePost: Action[PostEntity] = authenticatedAction.async(parse.json[PostEntity]) { request =>
    postDao.update(request.body).map(_ => ok).recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }

  def getOptions: Action[AnyContent] = authenticatedAction.async { _ =>
    val optionInfo = OptionInfo(Options.blogName, Options.blogDescription, Options.locales,
      Options.datetimeFormat, Options.defaultLocale, Options.pageSize, Options.disqusShortName,
      Options.gaId)
    Future(Ok(Json.toJson(optionInfo)))
  }

  def updateOptions: Action[OptionInfo] = authenticatedAction.async(parse.json[OptionInfo]) { request =>
    val options = request.body
    optionsDao.setBlogName(options.blogName)
    optionsDao.setBlogDescription(options.blogDescription)
    optionsDao.setLocales(options.locales)
    optionsDao.setDatetimeFormat(options.datetimeFormat)
    optionsDao.setDefaultLocale(options.defaultLocale)
    optionsDao.setPageSize(options.pageSize)
    optionsDao.setDisqusShortName(options.disqusShortName)
    optionsDao.setGAId(options.gaId)
    Future(ok)
  }

  def updatePassword: Action[JsValue] = authenticatedAction.async(parse.json) { request =>
    val oldPassword = (request.body \ "old").as[String]
    val newPassword = (request.body \ "new").as[String]
    userDao.updatePassword(request.user.username, newPassword, oldPassword).map { success =>
      if (success) ok else BadRequest(Json.obj("status" -> "err", "message" -> "Password not match"))
    }
  }

  def updateUser: Action[JsValue] = authenticatedAction.async(parse.json) { request =>
    val email = (request.body \ "email").as[String]
    val nickname = (request.body \ "nickname").as[String]
    userDao.update(UserEntity(request.user.username, "", Some(email), Some(nickname))).map { _ =>
      ok
    }
  }

  def blogrolls: Action[AnyContent] = authenticatedAction.async { _ =>
    Future(Ok(Json.toJson(Blogroll.all)))
  }

  def updateBlogrolls: Action[Seq[BlogrollEntity]] = authenticatedAction.async(parse.json[Seq[BlogrollEntity]]) { request =>
    blogrollDao.reset(request.body).map(_ => ok).recover {
      case t => BadRequest(Json.obj("status" -> "err", "message" -> t.getMessage))
    }
  }
}

object Admin {
  implicit val loginInfoReads: Reads[LoginInfo] = Json.reads[LoginInfo]
  implicit val optionInfoReads: Reads[OptionInfo] = Json.reads[OptionInfo]
  implicit val optionInfoWrites: OWrites[OptionInfo] = Json.writes[OptionInfo]
  implicit val postTagReads: Reads[PostTagEntity] = Json.reads[PostTagEntity]
  implicit val postTagWrites: OWrites[PostTagEntity] = Json.writes[PostTagEntity]
  implicit val postReads: Reads[PostEntity] = Json.reads[PostEntity]
  implicit val postWrites: OWrites[PostEntity] = Json.writes[PostEntity]
  implicit val blogrollReads: Reads[BlogrollEntity] = Json.reads[BlogrollEntity]
  implicit val blogrollWrites: OWrites[BlogrollEntity] = Json.writes[BlogrollEntity]
}