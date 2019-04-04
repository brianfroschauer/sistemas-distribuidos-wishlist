package routers

import akka.actor.ActorSystem
import akka.stream.Materializer
import example.myapp.helloworld.grpc.{AbstractGreeterServiceRouter, HelloReply, HelloRequest}
import javax.inject.Inject
import proto._
import repositories.{UserRepository, WishListRepository}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UserRouter @Inject()(mat: Materializer, system: ActorSystem, repo: WishListRepository)
                          (implicit ec: ExecutionContext)
  extends AbstractUserServiceRouter(mat, system) {


  override def addProduct(in: AddProductRequest): Future[AddProductResponse] = {
    repo.addProduct(in.userId, in.productId) map {
      w => AddProductResponse(w.productId)
    }
    //how can I manage a failure? use option?
  }

  //TODO managed in controller
  override def getProducts(in: GetProductsRequest): Future[GetProductsResponse] = {
    /*repo.getProducts(in.userId) map {
      w => AddProductResponse(w)
    }*/
    return null
  }

  override def deleteProduct(in: DeleteProductRequest): Future[DeleteProductResponse] = {
    repo.deleteProduct(in.userId, in.productId).map{
      id => DeleteProductResponse(id)
    }
    //how can I manage a failure? use option?
  }

}

case object UserNotFoundException extends RuntimeException