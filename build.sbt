name := "custom-metrics-in-spark-example"

version := "0.0.1"

scalaVersion := "2.10.4"

// additional libraries
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.2" % "provided"
  , "io.dropwizard.metrics" % "metrics-core" % "3.1.2" % "provided"
  , "io.dropwizard.metrics" % "metrics-jvm" % "3.1.2" % "provided"
  , "io.dropwizard.metrics" % "metrics-json" % "3.1.2" % "provided"
  , "nl.grons" %% "metrics-scala" % "3.5.5"
  , "org.eclipse.jetty" % "jetty-servlet" % "8.1.19.v20160209" % "provided"
)
