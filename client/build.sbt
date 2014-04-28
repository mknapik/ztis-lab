name := "client"


version := "0.0.2"

scalaVersion := "2.10.3"

// mainClass in Compile := Some("Rest")

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Sonatype (releases)" at "https://oss.sonatype.org/content/repositories/releases/"

javaOptions += "-Djava.library.path=/usr/local/lib"


libraryDependencies ++= Seq(
  "com.typesafe.akka"  %% "akka-actor"       % "2.2.3",
  "ch.qos.logback"      % "logback-classic"  % "1.0.13",
  "io.spray"            % "spray-can"        % "1.2.0",
  "io.spray"            % "spray-client"     % "1.2.0",
  "io.spray"           %% "spray-json"       % "1.2.5",
  "org.json4s"         %% "json4s-native"    % "3.2.7",
  "org.json4s"         %% "json4s-ext"       % "3.2.7",
  "io.spray"            % "spray-testkit"    % "1.2.0"        % "test",
  "org.scalatest"       % "scalatest_2.10"   % "2.1.3"        % "test",
  "com.typesafe.akka"  %% "akka-testkit"     % "2.2.3"        % "test"
)
