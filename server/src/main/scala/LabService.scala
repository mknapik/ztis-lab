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
      pathPrefix("problems") {
        pathEnd {
          complete {
            ""
          }
        }
      }
    }
}
