package metrics


import com.codahale.metrics.{ConsoleReporter, MetricRegistry}
import nl.grons.metrics.scala.{InstrumentedBuilder, MetricName}
import nl.grons.metrics.scala.{Counter => NlGronsCounter, Gauge => NlGronsGauge, Histogram => NlGronsHistogram,  Timer => NlGronsTimer}

import scala.concurrent.{ExecutionContext, Future}

//We're only abstracting the DropWizard metrics because we might want to
//use Application Instance metrics instead in some situations, and we'd
//like to be able to swap between the two without bothering the rest
//of the codebase.
sealed trait Metric

sealed trait Timer extends Metric {

  def timed[A](action: => A): A

  /**
   * Note that this will cause an additional call to the ExecutionContext
   */
  def timeFuture[A](future: => Future[A])(implicit context: ExecutionContext): Future[A]

}

sealed trait Counter {

  def dec(n: Long): Unit
  def dec: Unit
  def inc(n: Long): Unit
  def inc: Unit
  //Note: This might not be necessary in a common-denominator interface.
  def count: Long

}

sealed trait Histogram {
  def update(n: Long): Unit
}

sealed trait Gauge[T] {
  def value: T
}

//DropWizard implementations
private [metrics] case class DWTimerWrapper(timer: NlGronsTimer) extends Timer {

  def timed[A](action: => A): A = timer.time(action)

  def timeFuture[A](future: => Future[A])(implicit context: ExecutionContext): Future[A] = timer.timeFuture(future)

}

private [metrics] case class DWCounterWrapper(counter: NlGronsCounter) extends Counter {

  def inc(n: Long): Unit = counter.inc(n)
  def inc: Unit = inc(1)
  def dec(n: Long): Unit = counter.dec(n)
  def dec: Unit = dec(1)
  def count: Long = counter.count

}


private [metrics] case class DWHistogramWrapper(hist: NlGronsHistogram) extends Histogram {

  def update(n: Long): Unit = hist.+=(n)

}

private [metrics] case class DWGaugWrappere[T](gauge: NlGronsGauge[T]) extends Gauge[T]{

  def value: T = gauge.value

}



object MetricsFactory  {

  def counter(name: String): Counter = DWCounterWrapper(NlGrons.metrics.counter(name, null))

  def timer(name: String): Timer = DWTimerWrapper(NlGrons.metrics.timer(name, null))

  def histogram(name: String): Histogram = DWHistogramWrapper( NlGrons.metrics.histogram(name, null)   )

  def gauge[T](name: String)(f: =>T): Gauge[T] = DWGaugWrappere(NlGrons.metrics.gauge(name, null)(f))


}

// nl.grons.metrics adds some Scala tricks to the CodaHale/DropWizard metrics.
// It might be that we should use DropWizard metrics directly, rather than nl.grons
// metrics, in cases where we don't use any nl.grons niceties. As of this writing,
//the counter and histogram metrics don't use nl.grons syntax.
private [metrics] object NlGrons extends InstrumentedBuilder {

  //This sets the first name in the series of dot names.
  //We set it to "empty" here so that outside code can set the first name.
  override lazy val metricBaseName =  MetricName("")
  val metricRegistry: MetricRegistry = new MetricRegistry

}

// We need to allow  this backdoor to the MetricRegistry because Spark so
// that a Spark Source can use it.  We can't be a Spark Source ourselves (unless we move our package)
// because park Sources are private to the org.apache.spark package.
object BackDoor {
  def locateMetricRegistry: MetricRegistry = NlGrons.metricRegistry
}


