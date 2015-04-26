package controllers

import controllers.Actions._
import models._
import play.api.Play.current
import play.api.i18n._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._


object Application extends Controller {
  def index(lang: String) = LocalizedAsyncAction(lang) { implicit request =>
    for {
      locales <- Locales.all
      categories <- Categories.all
    } yield Ok(views.html.index("a", categories, locales))
  }

  def page(lang: String, slug: String) = LocalizedAction(lang) { implicit request =>
    Ok("hi")
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