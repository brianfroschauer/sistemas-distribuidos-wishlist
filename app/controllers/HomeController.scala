package controllers


import com.typesafe.config.Config
import example.myapp.helloworld.grpc.{GreeterServiceClient, HelloReply, HelloRequest}
import javax.inject.Inject
import play.api.mvc._
import product.{ProductRequest, ProductServiceClient}

import scala.concurrent.{ExecutionContext, Future}

class HomeController @Inject()(greeterServiceClient: GreeterServiceClient, productServiceClient: ProductServiceClient, config: Config)
                              (implicit ec: ExecutionContext) extends InjectedController {

  def index: Action[AnyContent] = Action.async {
    val request2 = ProductRequest(1)
    val reply2 = productServiceClient.getProduct(request2)
    reply2.map(_.name).map(m => Ok(m))
  }
}
