package controllers

import com.typesafe.config.Config
import example.myapp.helloworld.grpc.GreeterServiceClient
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.mvc._
import product.{ProductRequest, ProductServiceClient}
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class HomeController @Inject()(greeterServiceClient: GreeterServiceClient,
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

  def index: Action[AnyContent] = Action.async {
    val request2 = ProductRequest(1)
    val reply2 = productServiceClient.getProduct(request2)
    reply2.map(_.name).map(m => Ok(m))
  }
}

case class CreateUserForm(firstName: String, lastName: String)
