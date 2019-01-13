package controllers

import com.google.inject.Inject
import models.enums._
import play.api.i18n._
import play.api.mvc._

import scala.concurrent._


class Application @Inject()(postDao: dao.Post,
                            postTagDao: dao.PostTag,
                            localizedAction: LocalizedAction,
                            langs: Langs,
                            val controllerComponents: ControllerComponents)
                           (implicit val options: dao.Options,
                            implicit val blogrolls: dao.Blogroll,
                            implicit val ec: ExecutionContext) extends BaseController {

  def index(lang: String, page: Int): Action[AnyContent] = localizedAction.async { implicit request =>
    if (page <= 0) {
      Future.successful(Redirect(routes.Application.index(lang)))
    } else {
      for {
        (posts, postCount) <- postDao.listByPage(page)
      } yield Ok(views.html.index(posts, page, postCount))
    }
  }

  def post(lang: String, slug: String): Action[AnyContent] = localizedAction.async { implicit request =>
    val post = for {
      post <- postDao.getBySlug(slug) if post.postType == PostTypes.Post
      tags <- postTagDao.getBySlugs(post.tags)
    } yield Ok(views.html.post(post, tags))
    post.recover { case _ => NotFound(views.html.notFound()) }
  }

  def page(lang: String, slug: String) = localizedAction.async { implicit request =>
    val post = for {
      post <- postDao.getBySlug(slug) if post.postType == PostTypes.Page
    } yield Ok(views.html.page(post))
    post.recover { case _ => NotFound(views.html.notFound()) }
  }

  def tag(lang: String, slug: String, page: Int) = localizedAction.async { implicit request =>
    if (page <= 0) {
      Future(Redirect(routes.Application.tag(lang, slug, 1)))
    } else {
      for {
        (posts, postCount) <- postDao.listByTag(slug, if (page < 0) 1 else page)
        tag <- postTagDao.getBySlug(slug)
      } yield Ok(views.html.tag(tag, posts, page, postCount))
    }
  }

  def chooseLanguage: Action[AnyContent] = Action { implicit request =>
    request.acceptLanguages.map { lang =>
      if (lang.language == "zh") {
        Some(if (lang.country == "CN" || lang.country == "SG") "zh-Hans" else "zh-Hant")
      } else if (langs.availables.contains(lang)) {
        Some(lang.code)
      } else {
        None
      }
    }.find(_.isDefined).flatten match {
      case Some(code) => Redirect(routes.Application.index(code))
      case _ => Redirect(routes.Application.index(langs.availables.head.code))
    }
  }
}