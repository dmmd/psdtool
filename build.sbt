version := "0.1.0"

scalaVersion := "2.11.4"

sbtVersion := "0.13.7"

assemblyJarName in assembly := "psdtool.jar"

test in assembly := {}

mainClass in assembly := Some("edu.nyu.dlts.psd.Main")

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-native" % "3.2.11"
)