package controllers

import com.typesafe.config.Config
import example.myapp.helloworld.grpc.GreeterServiceClient
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.mvc._
import proto.ProductServiceClient
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(greeterServiceClient: GreeterServiceClient,
                               productServiceClient: ProductServiceClient,
                               userRepository: UserRepository,
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

  def addProductToUser: Unit = {
    // TODO Agregar un artículo a la lista de "items deseados" de un usuario.

    /* no esta hecha la tabla que realaciona un USER con un PRODUCT
     *
     * No se si hay que agregar un producto cualquiera que le pongas como JSON o si hay que consultar al server de
     * productos que hizo nacho, ver que productos hay (comunicación con grcp) y despues agregarlo a la lista de favoritos
     * */
  }

  def getProductsFromUserWith(userId: Long): Unit = {
    // TODO Recuperar la lista de un usuario y recuperar la lista de un usuario incluyend la descripción del producto.
    /*
     * Aca tiene que devolver la lista de id de los productos que tiene asociado el user
     */

  }

  def getProductsFromUserWithDescription(userId: Long): Unit = {
    // TODO Recuperar la lista de un usuario y recuperar la lista de un usuario incluyend la descripción del producto.
    /*
     * Aca es parecido al endpoint de arriba, con la diferencia:
     * En vez de devolver solo los id, se va a devolver productos (hay que ir a buscarlos al server de nacho)
     */

  }

  def deleteProductFromUser(userId: Long, productId: Long): Unit = {
    // TODO Eliminar artículos de la lista
    /*
     * Aca es eliminar el productId de la tabla user-product (que no esta creada todavía)
     */
  }
}

case class CreateUserForm(firstName: String, lastName: String)
