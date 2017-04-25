
val smackVersion = "4.1.9"

lazy val root = (project in file(".")).
  settings(
    name := "hubot-scala",
    version := "0.3.1",
    scalaVersion := "2.11.11",
    retrieveManaged := true,
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "org.slf4j" % "slf4j-api" % "1.7.22",
      "org.slf4j" % "log4j-over-slf4j" % "1.7.22",  // for any java classes looking for this
      "ch.qos.logback" % "logback-classic" % "1.1.9" ,
      "org.igniterealtime.smack" % "smack-core" % smackVersion,
      "org.igniterealtime.smack" % "smack-tcp" % smackVersion,
      "org.igniterealtime.smack" % "smack-im" % smackVersion,
      "org.igniterealtime.smack" % "smack-sasl-provided" % smackVersion,
      "org.igniterealtime.smack" % "smack-java7" % smackVersion,
      "org.igniterealtime.smack" % "smack-extensions" % smackVersion,
      "org.mapdb" % "mapdb" % "3.0.2",
      "org.scalaj" %% "scalaj-http" % "2.3.0",
      "org.json4s" %% "json4s-jackson" % "3.5.1",
      "org.scodec" %% "scodec-core" % "1.8.3",
      "org.scodec" %% "scodec-bits" % "1.0.11",
      "org.scalatest" %% "scalatest" % "3.0.3" % Test,
      "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % Test
    ),
    addCommandAlias("validate", Seq(
      "clean",
      "coverage",
      "test",
      "coverageReport",
      "coverageAggregate"
    ).mkString(";", ";", ""))
)


fork in Test := true // allow to apply extra setting to Test

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
