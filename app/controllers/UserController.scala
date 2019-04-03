package controllers

import com.typesafe.config.Config
import example.myapp.helloworld.grpc.GreeterServiceClient
import example.myapp.product.grpc.ProductServiceClient
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.mvc._
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
}

case class CreateUserForm(firstName: String, lastName: String)
