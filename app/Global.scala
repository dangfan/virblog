import models._
import play.api._
import play.api.libs.json._
import shared.DAO
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    DAO.db = Database.forConfig("database")

    StartData.createLocales()
    StartData.createCategories()
  }
}

object StartData {
  def createLocales(): Unit = {
    Locales.count map { size =>
      if (size == 0) {
        val locales = Seq(
          Locale("zhs", "简体中文"),
          Locale("zht", "台灣繁體"),
          Locale("en", "English"))
        locales.map(Locales.insert)
      }
    }
  }

  def createCategories(): Unit = {
    Categories.count map { size =>
      if (size == 0) {
        val categories = Seq(
          Category(None, Json.parse(""" {"zhs": "生活见闻", "zht": "生活見聞", "en": "My Life"} """)),
          Category(None, Json.parse(""" {"zhs": "信息技术", "zht": "資訊科技", "en": "IT"} """)),
          Category(None, Json.parse(""" {"zhs": "好奇心", "zht": "好奇心", "en": "Curiosity"} """))
        )
        categories.map(Categories.insert)
      }
    }
  }
}