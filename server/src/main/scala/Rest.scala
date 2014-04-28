import akka.actor.{Props, Address}
import akka.cluster.Cluster
import api.Api
import com.typesafe.config.ConfigFactory
import core.{BootedCore, CoreActors}
import web.Web
import model.Problem
import worker.{Frontend, Backend}

object Rest extends App with BootedCore with CoreActors with Api with Web with Backend {
  implicit lazy val frontend = system.actorOf(Props[Frontend], s"frontend")
  val joinAddress: Address = startBackend(None, "backend")
  val workers = Range(0, noOfWorkers).map {
    n => startWorker(n.toString, joinAddress, system)
  }
  val consumer = startFrontend("consumer", joinAddress, system)

  def config = ConfigFactory.parseString(s"akka.cluster.roles=[$role]").withFallback(ConfigFactory.load())

  implicit def role: String = "backend"

  Cluster(system).join(joinAddress)

  private lazy val noOfWorkers = ConfigFactory.load().getConfig("worker").getInt("nos")
//  private lazy val noOfWorkers = Runtime.getRuntime.availableProcessors() * 16
}
