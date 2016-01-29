lazy val root = (project in file(".")).
  settings(
    name := "hubot-scala",
    version := "0.0.3",
    scalaVersion := "2.11.7",
    retrieveManaged := true,
    libraryDependencies += "com.typesafe" % "config" % "1.3.0",
    libraryDependencies += "org.specs2" %% "specs2-core" % "3.7" % "test",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.1",
    libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.1",  // for any java classes looking for this
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.3" ,
    libraryDependencies += "io.evanwong.oss" % "hipchat-java" % "0.4.0",
    libraryDependencies += "org.igniterealtime.smack" % "smack-core" % "4.1.6",
    libraryDependencies += "org.igniterealtime.smack" % "smack-tcp" % "4.1.6",
    libraryDependencies += "org.igniterealtime.smack" % "smackx" % "3.2.1"
)

organization := "org.dberg"

//sonatypeProfileName := "org.dberg"

publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
 val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://github.com/denen99/hubot-scala</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:denen99/hubot-scala.git</url>
    <connection>scm:git:git@github.com:denen99/hubot-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>denen99</id>
      <name>Adam Denenberg</name>
      <url></url>
    </developer>
  </developers>)
