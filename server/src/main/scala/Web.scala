import core.{CoreActors, Core}
import api.Api
import akka.io.IO
import spray.can.Http
import util.Properties

/**
 * Provides the web server (spray-can) for the REST api in ``Api``, using the actor system
 * defined in ``Core``.
 *
 * You may sometimes wish to construct separate ``ActorSystem`` for the web server machinery.
 * However, for this simple application, we shall use the same ``ActorSystem`` for the
 * entire application.
 *
 * Benefits of separate ``ActorSystem`` include the ability to use completely different
 * configuration, especially when it comes to the threading model.
 */
trait Web {
  this: Api with CoreActors with Core =>

  val address = "0.0.0.0"
  val port = Properties.envOrElse("PORT", "8080").toInt

  IO(Http)(system) ! Http.Bind(rootService, address, port = port)

}
