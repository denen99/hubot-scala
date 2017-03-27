lazy val root = (project in file(".")).
  settings(
    name := "hubot-scala",
    version := "0.1.2",
    scalaVersion := "2.11.8",
    retrieveManaged := true,
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "org.specs2" %% "specs2-core" % "3.7" % "test",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "org.slf4j" % "slf4j-api" % "1.7.22",
      "org.slf4j" % "log4j-over-slf4j" % "1.7.22",  // for any java classes looking for this
      "ch.qos.logback" % "logback-classic" % "1.1.9" ,
      "org.igniterealtime.smack" % "smack-core" % "4.1.9",
      "org.igniterealtime.smack" % "smack-tcp" % "4.1.9",
//      "org.igniterealtime.smack" % "smackx" % "3.2.1",
      "org.igniterealtime.smack" % "smack-im" % "4.1.9",
      "org.igniterealtime.smack" % "smack-sasl-provided" % "4.1.9",
      "org.igniterealtime.smack" % "smack-java7" % "4.1.9",
      "org.igniterealtime.smack" % "smack-extensions" % "4.1.9",
      "org.mapdb" % "mapdb" % "3.0.2",
      "org.scalaj" %% "scalaj-http" % "2.3.0",
      "org.json4s" %% "json4s-jackson" % "3.5.1",
      "dnsjava" % "dnsjava" % "2.1.8"
    )
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
