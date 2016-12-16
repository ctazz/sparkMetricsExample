package org.apache.spark.source
//The Source trait is private to the org.apache.spark package, so our implementation must live here.

import com.codahale.metrics.MetricRegistry
import nl.grons.metrics.scala.InstrumentedBuilder
import org.apache.spark.metrics.source.Source

/**
 *
 * @param sourceName This value will appear in the dot name of your metrics.
 *                   It will be the first name you have control over,
 */
class CustomSource(val sourceName: String) extends Source with InstrumentedBuilder {

  override val metricRegistry = new MetricRegistry

  def counter(name: String) = metrics.counter(name, null)

  def timer(name: String) = metrics.timer(name, null)

  def histogram(name: String) = metrics.histogram(name, null)

  def gauge[T](name: String)(f: =>T) = metrics.gauge(name, null)(f)

}
