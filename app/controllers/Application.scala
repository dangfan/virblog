package controllers

import controllers.Actions._
import models._
import models.enums._
import play.api.Play.current
import play.api.i18n._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._


object Application extends Controller {
  def index(lang: String, page: Int) = LocalizedAsyncAction(lang) { implicit request =>
    if (page <= 0) {
      Future(Redirect(routes.Application.index(lang, 1)))
    } else {
      for {
        (posts, postCount) <- Posts.listByPage(page)
      } yield Ok(views.html.index(posts, page, postCount))
    }
  }

  def post(lang: String, slug: String) = LocalizedAsyncAction(lang) { implicit request =>
    val post = for {
      post <- Posts.getBySlug(slug) if post.postType == PostTypes.Post && post.status == PostStatuses.Published
      tags <- PostTags.getBySlugs(post.tags)
    } yield Ok(views.html.post(post, tags))
    post.recover { case _ => NotFound(views.html.notFound()) }
  }

  def page(lang: String, slug: String) = LocalizedAsyncAction(lang) { implicit request =>
    val post = for {
      post <- Posts.getBySlug(slug) if post.postType == PostTypes.Page && post.status == PostStatuses.Published
    } yield Ok(views.html.page(post))
    post.recover { case _ => NotFound(views.html.notFound()) }
  }

  def tag(lang: String, slug: String, page: Int) = LocalizedAsyncAction(lang) { implicit request =>
    for {
      (posts, postCount) <- Posts.listByTag(slug, if (page < 0) 1 else page)
      tag <- PostTags.getBySlug(slug)
    } yield Ok(views.html.tag(tag, posts, page, postCount))
  }

  def chooseLanguage = Action.async { implicit request =>
    request.acceptLanguages.map { lang =>
      if (lang.language == "zh") {
        Some(if (lang.country == "CN" || lang.country == "SG") "zh-Hans" else "zh-Hant")
      } else if (Lang.availables.contains(lang)) {
        Some(lang.code)
      } else {
        None
      }
    }.find(_.isDefined).flatten match {
      case Some(code) => Future(Redirect(routes.Application.index(code)))
      case _ => IpUtils.getLangCode(request.remoteAddress).map { code =>
        Redirect(routes.Application.index(code))
      }
    }
  }

  def migrate(lang: String, slug: String) = Action {
    lang match {
      case "zhs" => Redirect(routes.Application.post("zh-Hans", slug))
      case "zht" => Redirect(routes.Application.post("zh-Hant", slug))
      case _ => Redirect(routes.Application.post("en", slug))
    }
  }
}