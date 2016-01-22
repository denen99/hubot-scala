lazy val root = (project in file(".")).
  settings(
    name := "hubot-scala",
    version := "0.0.1",
    scalaVersion := "2.11.7",
    retrieveManaged := true,
    libraryDependencies += "com.typesafe" % "config" % "1.3.0",
    libraryDependencies += "org.specs2" %% "specs2-core" % "3.7" % "test",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.1",
    libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.1",  // for any java classes looking for this
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.3"
  )
