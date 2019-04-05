package controllers

import akka.actor.Status.Success
import com.typesafe.config.Config
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import play.api.mvc._
import proto.ProductServiceClient
import repositories.{UserRepository, WishListRepository}
import proto.{ProductReply, ProductRequest, ProductServiceClient}
import repositories.{UserRepository, WishListRepository}

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(productServiceClient: ProductServiceClient,
                               userRepository: UserRepository,
                               wishListRepository: WishListRepository,
                               wishlistRepository: WishListRepository,
                               config: Config)
                              (implicit ec: ExecutionContext) extends InjectedController {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  def addUser: Action[AnyContent] = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      _ => {
        Future.successful(Ok)
      },
      data => {
        val user = userRepository.create(data.firstName, data.lastName)
        user.map(p => Ok(Json.toJson(
          Map("status" -> "OK", "id" -> p.id.toString))))
      }
    )
  }

  val addProductToUserForm: Form[AddProductToUserForm] = Form {
    mapping(
      "userId" -> longNumber,
      "productId" -> longNumber
    )(AddProductToUserForm.apply)(AddProductToUserForm.unapply)
  }

  def addProductToUser = Action.async { implicit request =>

    addProductToUserForm.bindFromRequest.fold(
      _ => {
        Future.successful(Ok(views.html.index()))
      },
      data => {
        val wishlist = wishListRepository.addProduct(data.userId,data.productId)
        wishlist.map(w => Ok(Json.toJson(
          Map("status" -> "OK", "id" -> w.id.toString))))
      }
    )
  }

  /**
    * Recuperar la lista de un usuario y recuperar la lista de un usuario incluyend la descripción del producto.
    */
  def getProductsFromUserWith(userId: Long) = Action.async { implicit request =>
    wishListRepository.getProducts(userId).map(ids => Ok(Json.toJson(
      Map("status" -> "OK", "ids" -> ids.toString()))))
  }

  /**
    * Recuperar la lista de un usuario y recuperar la lista de un usuario incluyend la descripción del producto.
    */
  def getProductsFromUserWithDescription(userId: Long): Action[AnyContent] = Action.async{
    val result: Future[Seq[Future[ProductReply]]] = wishlistRepository.getProducts(userId).map(ids => ids.map(id => productServiceClient.getProduct(ProductRequest(id))))
    val result2: Future[Seq[ProductReply]] = result.flatMap(seq => Future.sequence(seq))
//   Nacho: Aca hice q se vayan a buscar los productos, falta hacer q los devuelva bien, osea pasar list a JSON
    result2.map(list => Ok(Json.toJson(list.toString())))

  }

  /**
    * Eliminar el productId de la tabla user-product
    */
  def deleteProductFromUser(userId: Long, productId: Long): Unit = {
    wishListRepository.deleteProduct(userId,productId).map(_ => Ok)
  }
}

case class CreateUserForm(firstName: String, lastName: String)

case class AddProductToUserForm(userId: Long, productId: Long)
