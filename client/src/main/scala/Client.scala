import akka.actor.{Props, Actor, ActorLogging}
import model.{Data, DataWrapper}
import scala.concurrent.Future
import scala.Some
import spray.http._
import scala.concurrent.duration._

import spray.http.HttpHeaders.`Content-Type`
import spray.http.ContentTypes.`application/json`
import spray.client.pipelining._
import spray.http.HttpRequest
import spray.http.HttpResponse

import scala.util.{Failure, Success}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol._
import akka.pattern.ask
import akka.util.Timeout

object Client {

  implicit val askTimeout = Timeout(5.seconds)

  def props(timeout: FiniteDuration = 5.seconds): Props =
    Props(classOf[Client], timeout)

  def main(args: Array[String]) = {
    import akka.actor._
    import scala.concurrent.duration._
    import model._
    import spray.http.HttpMethods

    val system = ActorSystem("system")

    val client = system.actorOf(Props(classOf[Client], 3.seconds), "client")
    import system.dispatcher

    val uri = "http://localhost:8080/data"
    val data = DataWrapper(Data(1, "data", "data"))

    client ! ((HttpMethods.POST, uri, data))
  }
}

class Client(timeout: FiniteDuration)
  extends Actor
  with ActorLogging {

  import SprayJsonSupport._

  implicit val planResultFormat = jsonFormat3(Data)
  implicit val planResultsFormat = jsonFormat1(DataWrapper)

  import context.dispatcher


  implicit val httpTimeout = Timeout(timeout)

  val pipeline: HttpRequest => Future[HttpResponse] = (
    addHeader(`Content-Type`(`application/json`))
      ~> sendReceive
    )

  override def receive = {
    case (method: HttpMethod, uri: String, DataWrapper(data)) =>
      log.info(s"Sending results with $method to $uri")
      val request: Option[HttpRequest] = method match {
        case HttpMethods.POST => Some(Post(uri, DataWrapper(data)))
        case HttpMethods.GET => Some(Get(uri, DataWrapper(data)))
        case m =>
          log.warning(s"Unsupported HttpMethod: $m")
          None
      }
      if (request.isDefined) {
        val response: Future[HttpResponse] = pipeline(request.get)
        response.onComplete {
          case Success(r) =>
            sender ! "ok"
            log.debug(s"Request $request responded with ${r.status}")
            context.system.shutdown()
          case Failure(error) =>
            sender ! "failure"
            log.error(error, "Couldn't send results!")
            context.system.shutdown()
        }
      }
    case unexpected =>
      log.debug("unmatched")
      log.debug(unexpected.toString)
  }
}
