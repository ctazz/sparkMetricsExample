###Purpose
Show how to get application-specific metrics to show up along with Spark metrics.
Abstract some basic DropWizard metrics for a project that may possibly use another metrics api in certain environments.

###Build it:
sbt clean assembly

###Submit it:
<yourPath>/bin/spark-submit --class example.WordCount --master local[4]  ./target/scala-2.10/custom-metrics-in-spark-example-assembly-0.0.1.jar

###Now watch your customer Counter metric appear in the console.
You can also configure src/main/resources/metrics.properties to show your metrics in other sinks.
You can also see your metrics in http://localhost:4040/metrics/json/