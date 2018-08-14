import sbt.Keys.{libraryDependencies, publishMavenStyle, scalacOptions}
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

lazy val scalaV = "2.12.6"

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

    libraryDependencies += "io.monix" %%% "monix" % "3.0.0-RC1",

    libraryDependencies += "org.scalactic" %%% "scalactic" % "3.0.5",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.5" % "test",

  ).
  jvmSettings(
  /**  libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.14",
      "com.typesafe.akka" %% "akka-stream" % "2.5.14"
    )*/
  ).
  jsSettings(
    resolvers += Resolver.sonatypeRepo("releases"),
    /**libraryDependencies ++= Seq(
      "org.akka-js" %%% "akkajsactorstream" % "1.2.5.14",
      "org.akka-js" %%% "akkajsactor" % "1.2.5.14",
      "com.lihaoyi" %%% "scalatags" % "0.6.4"
    ),*/
    libraryDependencies += "org.querki" %%% "querki-jsext" % "0.8",
    libraryDependencies += "com.zeiss" %%% "johnny5scala-js" % "0.0.2-SNAPSHOT",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    skip in packageJSDependencies := false,
    scalaJSUseMainModuleInitializer := true,
      scalacOptions ++= Seq(
        "-P:scalajs:sjsDefinedByDefault"
      )
  )
