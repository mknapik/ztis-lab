import akka.actor.{ActorSystem, Props, Actor, ActorLogging}
import model.{Data, DataWrapper}
import scala.concurrent.Future
import spray.http._
import scala.concurrent.duration._
import akka.util.Timeout

import spray.http.HttpHeaders.`Content-Type`
import spray.http.ContentTypes.`application/json`
import spray.client.pipelining._
import spray.httpx.encoding.{Deflate, Gzip}

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol._
import scala.util.{Failure, Success}

object Client {
  def props(timeout: FiniteDuration = 5.seconds): Props =
    Props(classOf[Client], timeout)
  def main(args: Array[String]) {
    val system  = ActorSystem("system")
    
    val client = system.actorOf(Props[Client])

    val uri = "www.google.com"
    val data = DataWrapper(Data(1, "data", "data"))

    client ! (HttpMethods.GET, uri, data)
  }
}

class Client(timeout: FiniteDuration)
  extends Actor
  with ActorLogging {

  import Client._

  import context.dispatcher

  import SprayJsonSupport._

  implicit val httpTimeout = Timeout(timeout)

  val pipeline: HttpRequest => Future[HttpResponse] = (
    addHeader(`Content-Type`(`application/json`))
      ~> sendReceive
    )

  override def receive = {
    case (method: HttpMethod, uri: String, data: String) =>
      log.info(s"Sending results with $method to $uri")
      val request: Option[HttpRequest] = method match {
        case HttpMethods.POST => Some(Post(uri, data))
        case HttpMethods.GET => Some(Get(uri, data))
        case HttpMethods.PATCH => Some(Patch(uri, data))
        case HttpMethods.PUT => Some(Put(uri, data))
        case m =>
          log.warning(s"Unsupported HttpMethod: $m")
          None
      }
      if (request.isDefined) {
        val response: Future[HttpResponse] = pipeline(request.get)
        response.onComplete {
          case Success(r) =>
            log.debug(s"Request $request responded with ${r.status}")
          case Failure(error) =>
            log.error(error, "Couldn't push results!")
        }
      }
  }
}
