name := "Virblog"

version := "1.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  guice,
  "org.slf4j"            % "slf4j-nop"             % "1.7.12",
  "org.postgresql"       % "postgresql"            % "42.2.5",
  "com.typesafe.play"   %% "play-slick"            % "3.0.1",
  "com.typesafe.play"   %% "play-slick-evolutions" % "3.0.1",
  "com.typesafe.slick"  %% "slick-hikaricp"        % "3.2.3",
  "com.github.tminglei" %% "slick-pg"              % "0.17.0",
  "com.vladsch.flexmark" % "flexmark-all"          % "0.40.2",
  "net.java.dev.jna"     % "jna"                   % "5.1.0",
  "com.github.t3hnar"   %% "scala-bcrypt"          % "3.1",
  "com.pauldijou"       %% "jwt-play-json"         % "1.0.0"
)
