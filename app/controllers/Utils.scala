package controllers


object Utils {
  def getNewLocalizedUrl(newLang: String)(implicit request: LocalizedRequest): String = {
    val url = """/([\w-]+)/.*""".r
    request.request.path match {
      case url(oldLang, _*) => request.request.path.replace(oldLang, newLang)
      case _ => "/"
    }
  }
}
