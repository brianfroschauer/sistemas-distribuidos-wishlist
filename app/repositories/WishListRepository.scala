package repositories

import javax.inject.{Inject, Singleton}
import models.WishList
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WishListRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class WishListTable(tag: Tag) extends Table[WishList](tag, "wish_list") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("user_id")
    def productId = column[Long]("product_id")
    def * = (id, userId, productId) <> ((WishList.apply _).tupled, WishList.unapply)
  }

  private val wishList = TableQuery[WishListTable]

  def addProduct(userId: Long, productId: Long): Future[WishList] = db.run {
    (wishList.map(u => (u.userId, u.productId))
      returning wishList.map(_.id)
      into ((data, id) => WishList(id, data._1, data._2))
      ) += (userId, productId)
  }

  def getProducts(userId: Long): Unit = {
    // TODO
  }

  def deleteProduct(userId: Long, productId: Long): Unit = {
    // TODO
  }
}
