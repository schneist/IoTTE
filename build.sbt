import sbt.Keys.{libraryDependencies, publishMavenStyle, scalacOptions}
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val scalaV = "2.12.7"

lazy val dslJVM = dsl.jvm
lazy val dslJS = dsl.js

lazy val dsl =  crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full).in(file(".")).
  settings(
    name := "IoTTE",

    organization := "com.zeiss",

    version := "0.1-SNAPSHOT",

    scalaVersion := scalaV,

      scalacOptions ++= Seq(
        "-feature",
        "-Ypartial-unification" ,
      ),

    resolvers ++= Seq(
      "Typesafe" at "http://repo.typesafe.com/typesafe/releases/",
      "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Public" at "https://oss.sonatype.org/content/repositories/public",
      "JFrog" at  "http://repo.jfrog.org/artifactory/libs-releases/",
      "JBoss" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
      "MVNSearch" at "http://www.mvnsearch.org/maven2/"
    ),

    publishMavenStyle := true,

    libraryDependencies += "io.monix" %%% "monix" % "3.0.0-RC2",
    
    libraryDependencies += "org.scalactic" %%% "scalactic" % "3.0.5",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.5" % "test",

  ).
  jvmSettings(
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.17",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.17",
    libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.5",
    libraryDependencies += "junit" % "junit" % "4.10" % Test,
    libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.17" % Test

  ).
  jsSettings(
    resolvers += Resolver.sonatypeRepo("releases"),
    /**libraryDependencies ++= Seq(
      "org.akka-js" %%% "akkajsactorstream" % "1.2.5.14",
      "org.akka-js" %%% "akkajsactor" % "1.2.5.14",
      "com.lihaoyi" %%% "scalatags" % "0.6.4"
    ),*/
    libraryDependencies += "io.scalajs" %%% "nodejs" % "0.4.2",
    libraryDependencies += "org.querki" %%% "querki-jsext" % "0.8",
    libraryDependencies += "com.zeiss" %%% "johnny5scala-js" % "0.0.2-SNAPSHOT",
    libraryDependencies += "fr.hmil" %%% "roshttp" % "2.1.0",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    skip in packageJSDependencies := false,
    scalaJSUseMainModuleInitializer := true,
      scalacOptions ++= Seq(
        "-P:scalajs:sjsDefinedByDefault"
      )
  )

mappings in Universal in Docker += artifactPath.in(fullOptJS).in(Compile).in(dslJS).value -> "iotte.js"


enablePlugins(DockerPlugin)

import com.typesafe.sbt.packager.docker._

dockerCommands := Seq(
  Cmd("FROM", "node"),
  Cmd("RUN", s"""npm install johnny-five"""),
  Cmd("COPY", "iotte.js", "."),
  ExecCmd("CMD", "node iotte.js")
)