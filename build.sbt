import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}



lazy val dsl =  crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full).in(file(".")).
  settings(
    name := "IoTTE",

    organization := "net.novogarchinsk",

    version := "0.1-SNAPSHOT",

    scalaVersion := "2.12.8",

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
    libraryDependencies += "org.scalaz" %%% "scalaz-zio" % "0.5.3",
    libraryDependencies += "org.scalactic" %%% "scalactic" % "3.0.5",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.5" % "test",
    libraryDependencies +="com.chuusai" %%% "shapeless" % "2.3.3",

  ).
  jvmSettings(

  ).
  jsSettings(
    resolvers += Resolver.sonatypeRepo("releases"),
    libraryDependencies += "io.scalajs" %%% "nodejs" % "0.4.2",
    libraryDependencies += "org.querki" %%% "querki-jsext" % "0.8",
    libraryDependencies += "com.zeiss" %%% "johnny5scala-js" % "0.0.2-SNAPSHOT",
    libraryDependencies += "fr.hmil" %%% "roshttp" % "2.2.3",
    libraryDependencies += "net.novogarchinsk" %%% "pyshell-scalajs" % "0.1-SNAPSHOT",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    skip in packageJSDependencies := false,
    scalaJSUseMainModuleInitializer := true,
    scalacOptions ++= Seq(
      "-P:scalajs:sjsDefinedByDefault"
    )
  )


/*
mappings in Universal in Docker += artifactPath.in(fullOptJS).in(Compile).in(dslJS).value -> "iotte.js"


enablePlugins(DockerPlugin)

import com.typesafe.sbt.packager.docker._

//ToDo:: npm modules in sc
dockerCommands := Seq(
  Cmd("FROM", "node"),
  Cmd("RUN", s"""npm install johnny-five"""),
  Cmd("COPY", "iotte.js", "."),
  ExecCmd("CMD", "node iotte.js")
)
*/