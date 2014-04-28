import com.typesafe.config.ConfigFactory

object Rest extends App with BootedCore with CoreActors with Api with Web {

  def config = ConfigFactory.load()

}
