package controllers

import com.google.inject.Inject
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class I18n @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def zhs2Zht = Action.async(BodyParsers.parse.json) { request =>
//    var zht = Utils.zhs2Zht((request.body \ "content").as[String])
//    zht = zht.replace('“', '「')
//    zht = zht.replace('”', '」')
//    zht = zht.replace('‘', '『')
//    zht = zht.replace('’', '』')
//    Future(Ok(zht))
    Future.successful(Ok(""))
  }

}
