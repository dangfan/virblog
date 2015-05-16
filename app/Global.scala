import controllers.LocalizedRequest
import models._
import play.api._
import play.api.mvc.Results._
import play.api.mvc._
import shared.DAO
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object Global extends GlobalSettings {
  override def onStart(app: Application) = {
    DAO.db = Database.forConfig("database")
    Options.load()
    Blogrolls.load()

    Users.count.map { count =>
      if (count == 0) {
        Users.create("admin", "admin")
      }
    }
  }

  override def onStop(app: Application) = {
    DAO.db.close()
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    val url = """[\w-]+""".r
    val lang = url.findFirstIn(request.path).getOrElse(Options.defaultLocale)
    val localizedRequest = LocalizedRequest(lang, "", Request[AnyContent](request, AnyContentAsEmpty))
    Future.successful(NotFound(views.html.notFound()(localizedRequest)))
  }
}