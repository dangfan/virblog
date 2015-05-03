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
    for {
      posts <- Posts.listByPage(if (page < 0) 1 else page)
      postCount <- Posts.count
    } yield Ok(views.html.index(posts, page, postCount))
  }

  def post(lang: String, slug: String) = LocalizedAsyncAction(lang) { implicit request =>
    val post = for {
      post <- Posts.getBySlug(slug) if post.postType == PostType.Post && post.status == PostStatus.Published
      tags <- PostTags.getBySlugs(post.tags)
    } yield Ok(views.html.post(post, tags))
    post.recover { case _ => NotFound(views.html.notFound()) }
  }

  def page(lang: String, slug: String) = LocalizedAsyncAction(lang) { implicit request =>
    val post = for {
      post <- Posts.getBySlug(slug) if post.postType == PostType.Page && post.status == PostStatus.Published
    } yield Ok(views.html.page(post))
    post.recover { case _ => NotFound(views.html.notFound()) }
  }

  def chooseLanguage = Action.async { implicit request =>
    request.acceptLanguages.find(Lang.availables.contains) match {
      case Some(lang) => Future {
        Redirect(routes.Application.index(lang.code))
      }
      case None => IP2Languages.getLangCode(request.remoteAddress).map { code =>
        Redirect(routes.Application.index(code))
      }
    }
  }
}