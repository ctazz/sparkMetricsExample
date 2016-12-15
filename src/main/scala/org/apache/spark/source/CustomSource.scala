package org.apache.spark.source
//The Source trait is private to the org.apache.spark package, so our implementation must live here.

import org.apache.spark.metrics.source.Source


class CustomSource extends Source{

  //This will appear in the metrics dot name. It will be the first name you have control over,
  //after the spark information (e.g. local-1481836791890.driver.custom.metricName
  override val sourceName = "custom"

  override val metricRegistry =  metrics.BackDoor.locateMetricRegistry


}
