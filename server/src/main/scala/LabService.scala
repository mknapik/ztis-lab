import model.{DataWrapper, Data}
import spray.http.StatusCodes
import spray.routing.Directives
import scala.concurrent.ExecutionContext
import spray.httpx.Json4sSupport
import org.slf4j.LoggerFactory

class LabService()(implicit executionContext: ExecutionContext)
  extends Directives with Json4sSupport {
  lazy val log = LoggerFactory.getLogger(this.getClass)

  implicit def json4sFormats = org.json4s.DefaultFormats + org.json4s.ext.DurationSerializer

  import akka.util.Timeout
  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)

  val route =
    get {
      pathPrefix("data") {
        pathEnd {
          complete {
            DataWrapper(Data(1, "int", "2"))
          }
        }
      }
    } ~ post {
      pathPrefix("data") {
        pathEnd {
          entity(as[DataWrapper]) {
            request => {
              try {
                log.debug(request.toString)
                complete((StatusCodes.Created, request))
              } catch {
                case e: RuntimeException =>
                  log.error(e.getStackTraceString)
                  complete(StatusCodes.BadRequest)
              }
            }
          }
        }
      }
    }
}

