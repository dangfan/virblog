import play.api._
import shared.DAO
import slick.jdbc.JdbcBackend.Database

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    DAO.db = Database.forConfig("database")
  }

  override def onStop(app: Application): Unit = {
    DAO.db.close()
  }
}