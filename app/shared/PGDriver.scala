package shared

import slick.driver.PostgresDriver
import com.github.tminglei.slickpg._

object PGDriver extends PostgresDriver
                   with PgPlayJsonSupport
                   with array.PgArrayJdbcTypes {
  override val pgjson = "jsonb"

  override val api = new API with JsonImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
  }

  val plainAPI = new API with PlayJsonPlainImplicits
}