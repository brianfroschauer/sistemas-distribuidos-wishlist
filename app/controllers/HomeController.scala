package controllers


import com.typesafe.config.Config
import example.myapp.helloworld.grpc.{ GreeterServiceClient, HelloReply, HelloRequest }
import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

class HomeController @Inject()(greeterServiceClient: GreeterServiceClient, config: Config)
                              (implicit ec: ExecutionContext) extends InjectedController {

  def index: Action[AnyContent] = Action.async {
    val request = HelloRequest("Caplin")
    val reply: Future[HelloReply] = greeterServiceClient.sayHello(request)
    reply.map(_.message).map(m => Ok(m))
  }
}
