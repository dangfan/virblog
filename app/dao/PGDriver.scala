package dao

import com.github.tminglei.slickpg._
import models.enums.{PostStatuses, PostTypes}

trait PGDriver extends ExPostgresProfile
                  with PgArraySupport
                  with PgDate2Support
                  with PgHStoreSupport
                  with PgEnumSupport {

  override val api = new API with DateTimeImplicits
                             with HStoreImplicits
                             with ArrayImplicits
                             with EnumImplicits {}

  trait EnumImplicits {
    implicit val postStatusTypeMapper = createEnumJdbcType("PostStatus", PostStatuses, quoteName = true)
    implicit val postTypeTypeMapper = createEnumJdbcType("PostType", PostTypes, quoteName = true)
  }

}

object PGDriver extends PGDriver
