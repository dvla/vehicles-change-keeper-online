import java.io.File
import java.util.Date

import sbt.Keys._
import sbt._

object Common {
  val versionString = "0.1-SNAPSHOT"
  val scalaVersionString = "2.10.3"
  val organisationString = "dvla"
  val organisationNameString = "Driver & Vehicle Licensing Agency"
  val nexus = "http://rep002-01.skyscape.preview-dvla.co.uk:8081/nexus/content/repositories"

  val scalaOptionsSeq = Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-Xlint",
    "-language:reflectiveCalls",
    "-Xmax-classfile-name", "128"
  )

  val projectResolvers = Seq(
    "typesafe repo" at "http://repo.typesafe.com/typesafe/releases",
    "spray repo" at "http://repo.spray.io/",
    "local nexus snapshots" at s"$nexus/snapshots",
    "local nexus releases" at s"$nexus/releases"
  )

  val publishResolver: sbt.Def.Initialize[Option[sbt.Resolver]] = version { v: String =>
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at s"$nexus/snapshots")
    else
      Some("releases" at s"$nexus/releases")
  }

  val sbtCredentials = Credentials(Path.userHome / ".sbt/.credentials")

  def prop(name: String) = sys.props.getOrElse(name, "Unknown")
  def buildDetails(name: String, version: String): String =
    s"""Project: $name-$version by $organisationNameString
       |Build on: ${new Date()} by ${prop("user.name")}@${java.net.InetAddress.getLocalHost.getHostName}
       |Build OS: ${prop("os.name")}-${prop("os.version")}
       |Build Java version: ${prop("java.version")} ${prop("java.vendor")}
    """.stripMargin

  def saveBuildDetails(root: Project) = Def.task {
    val buildDetailsName = "build-details.txt"
    val buildDetailsFile = new File(classDirectory.in(root).in(Compile).value, buildDetailsName)
    IO.write(buildDetailsFile, buildDetails(name.in(root).value, version.in(root).value))
    println(s"Build details written to: $buildDetailsFile \n ${buildDetails(name.in(root).value, version.in(root).value)}")
    Seq((new File(resourceDirectory.in(root).in(Compile).value, buildDetailsName), buildDetailsFile))
  }
}
