package repositories

import com.sun.org.apache.xpath.internal.operations.Bool
import javax.inject.{Inject, Singleton}
import models.WishList
import org.h2.tools.SimpleResultSet.SimpleArray
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.QueryBase

import scala.concurrent.{ExecutionContext, Future}
//import slick.jdbc.PostgresProfile

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

//    def product = foreignKey("prod_fk", productIds, products)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }

  private val wishList = TableQuery[WishListTable]


  /*private class ProductWrapperTable(tag: Tag) extends Table[(Long, Long)](tag, "product_wrapper") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def prodId = column[Long]("product_id")

    def * = (id, prodId)
  }

  private val products = TableQuery[ProductWrapperTable]

  val addProductSetup = DBIO.seq(

    val prodWrapperId = (products returning products.map(_.id)) += (123)

  )*/

  //If present update row, if not insert new row
  def addProduct(userId: Long, productId: Long): Future[WishList] = db.run {
    (wishList.map(u => (u.userId, u.productId))
      returning wishList.map(_.id)
      into ((data, id) => WishList(id, data._1, data._2))
      ) += (userId, productId)
  }

  def getProducts(userId: Long): Future[Seq[Long]] = db.run {
    wishList.filter{
      _.userId === userId
    }.map{_.productId}.result
  }

  def deleteProduct(userId: Long, productId: Long): Unit = db.run(
    wishList.filter{ w => w.productId === productId && w.userId === userId}.delete
  )
}
