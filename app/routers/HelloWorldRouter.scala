package routers

import akka.actor.ActorSystem
import akka.stream.Materializer
import example.myapp.helloworld.grpc.AbstractGreeterServiceRouter
import example.myapp.helloworld.grpc.{ HelloReply, HelloRequest }
import javax.inject.Inject

import scala.concurrent.Future

class HelloWorldRouter @Inject()(mat: Materializer, system: ActorSystem)
    extends AbstractGreeterServiceRouter(mat, system) {

  override def sayHello(in: HelloRequest): Future[HelloReply] =
    Future.successful(HelloReply(s"Hello, ${in.name}!"))
}
