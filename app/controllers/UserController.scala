package controllers

import akka.actor.Status.Success
import com.typesafe.config.Config
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
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
        user.map(p => Ok(p.id.toString))
      }
    )
  }

  val addProductToUserForm: Form[AddProductToUserForm] = Form {
    mapping(
      "userId" -> longNumber,
      "productId" -> longNumber
    )(AddProductToUserForm.apply)(AddProductToUserForm.unapply)
  }

  //TODO DEPRECATED
  def addProductToUser = Action.async { implicit request =>

    addProductToUserForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index()))
      },
      data => {
        val wishlist = wishListRepository.addProduct(data.userId,data.productId)
        wishlist.map(w => Ok(w.id.toString))
      }
    )

    /* no esta hecha la tabla que realaciona un USER con un PRODUCT
     *
     * No se si hay que agregar un producto cualquiera que le pongas como JSON o si hay que consultar al server de
     * productos que hizo nacho, ver que productos hay (comunicación con grcp) y despues agregarlo a la lista de favoritos
     * */
  }

  //TODO DEPRECATED ??
  def getProductsFromUserWith(userId: Long) = Action.async { implicit request =>
    // TODO Recuperar la lista de un usuario y recuperar la lista de un usuario incluyend la descripción del producto.
    wishListRepository.getProducts(userId).map(ids => Ok(ids.toString()))
    /*
     * Aca tiene que devolver la lista de id de los productos que tiene asociado el user
     */
  }

  def getProductsFromUserWithDescription(userId: Long): Action[AnyContent] = Action.async{
    // TODO Recuperar la lista de un usuario y recuperar la lista de un usuario incluyend la descripción del producto.
    /*
     * Aca es parecido al endpoint de arriba, con la diferencia:
     * En vez de devolver solo los id, se va a devolver productos (hay que ir a buscarlos al server de nacho)
     */
    val result: Future[Seq[Future[ProductReply]]] = wishlistRepository.getProducts(userId).map(ids => ids.map(id => productServiceClient.getProduct(ProductRequest(id))))
    val result2: Future[Seq[ProductReply]] = result.flatMap(seq => Future.sequence(seq))
//   Nacho: Aca hice q se vayan a buscar los productos, falta hacer q los devuelva bien, osea pasar list a JSON
    result2.map(list => Ok(list.toString()))

  }

  //TODO DEPRECATED
  def deleteProductFromUser(userId: Long, productId: Long): Unit = {
    wishListRepository.deleteProduct(userId,productId).map(w => Ok(w.toString))
    /*
     * Aca es eliminar el productId de la tabla user-product (que no esta creada todavía)
     */
  }
}

case class CreateUserForm(firstName: String, lastName: String)

case class AddProductToUserForm(userId: Long, productId: Long)
