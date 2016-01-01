name := "Virblog"

version := "1.0"

lazy val `virblog` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  specs2 % Test,
  "org.slf4j"            % "slf4j-nop"             % "1.7.12",
  "org.postgresql"       % "postgresql"            % "9.4-1204-jdbc42",
  "com.typesafe.play"   %% "play-slick"            % "1.1.1",
  "com.typesafe.play"   %% "play-slick-evolutions" % "1.1.1",
  "com.typesafe.slick"  %% "slick-hikaricp"        % "3.1.1",
  "com.github.tminglei" %% "slick-pg"              % "0.10.2",
  "com.github.tminglei" %% "slick-pg_date2"        % "0.10.2",
  "org.pegdown"          % "pegdown"               % "1.5.0",
  "com.roundeights"     %% "hasher"                % "1.0.0",
  "net.java.dev.jna"     % "jna"                   % "4.1.0"
)

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "RoundEights" at "http://maven.spikemark.net/roundeights"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
