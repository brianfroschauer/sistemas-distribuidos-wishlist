package models

import play.api.libs.json.{Json, OFormat}

case class User(id: Long, firstName: String, lastName: String)

object User {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}
