package org.apache.spark.source
//The Source trait is private to the org.apache.spark package, so our implementation must live here.

import org.apache.spark.metrics.source.Source

/**
 *
 * @param sourceName This value will appear in the dot name of your metrics.
 *                   It will be the first name you have control over,
 */
class CustomSource(val sourceName: String) extends Source{

  override val metricRegistry =  metrics.BackDoor.locateMetricRegistry

}
