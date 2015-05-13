package models.enums

import play.api.libs.json._


object PostTypes extends Enumeration {
  type PostType = Value
  val Post, Page = Value

  implicit def reads: Reads[PostType] = new Reads[PostType] {
    def reads(json: JsValue): JsResult[PostType] = json match {
      case JsString(v) => JsSuccess(PostTypes.withName(v))
      case _ => JsError("String value expected")
    }
  }

  implicit def writes: Writes[PostType] = new Writes[PostType] {
    def writes(v: PostType): JsValue = JsString(v.toString)
  }
}