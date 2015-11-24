/*
 * Copyright 2015 Databricks Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import scala.util.Properties

import sbt._
import sbt.Keys._


object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.apache.spark",
    version := "0.1.6",
    scalaVersion := "2.11.7",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.sonatypeRepo("releases"),
      Resolver.typesafeRepo("releases"),
      "JAnalyse Repository" at "http://www.janalyse.fr/repository/"
    ),
    parallelExecution in Test := false,
    // This fork avoids "Native Library already loaded in another classloader" errors:
    fork in Test := true,
    publishTo := Some(Resolver.file("file",
      new File(Path.userHome.absolutePath + "/.m2/repository")))
  )
}


object SparkIntegrationTestsBuild extends Build {

  import BuildSettings._

  lazy val root = Project(
    "spark-integration-tests",
    file("."),
    settings = buildSettings ++ Seq(
      javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
      libraryDependencies ++= Seq(
        "com.jsuereth" %% "scala-arm" % "1.4",
        "fr.janalyse"   %% "janalyse-ssh" % "0.9.14",
        "com.jcraft" % "jsch" % "0.1.51",
        "org.apache.spark" %% "spark-core" % "1.5.2",
        "org.apache.spark" %% "spark-streaming" % "1.5.2",
        "org.scalatest" %% "scalatest" % "2.2.1" % "test",
        "net.sf.jopt-simple" % "jopt-simple" % "3.2" % "test"  // needed by Kafka, excluded by Spark
      )
    )
  )// .dependsOn(sparkCore, sparkStreaming, streamingKafka)
}
