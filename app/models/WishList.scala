package models

import play.api.libs.json.{Json, OFormat}

case class WishList(id: Long, userId: Long, productId: Long)

object WishList {
  implicit val wishListFormat: OFormat[WishList] = Json.format[WishList]
}
