package models.enums

import play.api.libs.json._

object PostStatuses extends Enumeration {
  type PostStatus = Value
  val Published, Draft = Value

  implicit def reads: Reads[PostStatus] = new Reads[PostStatus] {
    def reads(json: JsValue): JsResult[PostStatus] = json match {
      case JsString(v) => JsSuccess(PostStatuses.withName(v))
      case _ => JsError("String value expected")
    }
  }

  implicit def writes: Writes[PostStatus] = new Writes[PostStatus] {
    def writes(v: PostStatus): JsValue = JsString(v.toString)
  }
}