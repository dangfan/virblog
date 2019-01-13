package controllers

import com.google.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc._


class I18n @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def zhs2Zht: Action[JsValue] = Action(parse.json) { request =>
    var zht = Utils.zhs2Zht((request.body \ "content").as[String])
    zht = zht.replace('“', '「')
    zht = zht.replace('”', '」')
    zht = zht.replace('‘', '『')
    zht = zht.replace('’', '』')
    Ok(zht)
  }

}
