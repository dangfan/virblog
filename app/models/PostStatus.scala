package models

import slick.driver.H2Driver.api._


object PostStatus extends Enumeration {
  type PostStatus = Value
  val Published, Draft = Value

  implicit val postStatusMapper = MappedColumnType.base[PostStatus, Int](
    s => if (s == PostStatus.Published) 1 else 0,
    i => if (i == 1) PostStatus.Published else PostStatus.Draft
  )
}