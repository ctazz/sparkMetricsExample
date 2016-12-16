
package example

import org.apache.spark.source.CustomSource
import org.apache.spark.{SparkConf, SparkEnv, SparkContext}
import org.apache.spark.rdd.RDD

object WordCount {
    def main(args: Array[String]) {

      val conf = new SparkConf().setAppName("wordCount")
      conf.set("spark.metrics.conf", "src/main/resources/metrics.properties")
      val sc = new SparkContext(conf)

      val customSource1 = new CustomSource("custom.subpath1")
      val customSource2 = new CustomSource("custom.subpath2")

      //a metric must be created before its associated source registry is registered!
      val counter1  =  customSource1.counter("ourOwnCounter")
      SparkEnv.get.metricsSystem.registerSource(customSource1)
      val counter2  =  customSource2.counter("ourOwnCounter")
      SparkEnv.get.metricsSystem.registerSource(customSource2)



      val inputFile = "src/main/resources/count.txt" //or you could do args(0)

      while(true) {

        val startTime = System.currentTimeMillis


        //Most of this logic is taken from  the book "Learning Spark"
        val input: RDD[String] = sc.textFile(inputFile)
        val words: RDD[String] = input.flatMap(line => line.split(" "))
        val counts: RDD[((String, Int), Long)] = words.map(word => (word, 1)).reduceByKey { case (x, y) => x + y }.
          sortBy(_._2, false).
          zipWithIndex.
          filter(_._2 <= 10).repartition(1)

        counts.glom.foreach(_.foreach(x => println(x)))

        println("time to complete is " + (System.currentTimeMillis - startTime))

        counter1.inc(1)
        counter2.inc(1)

        Thread.sleep(4000)
      }


    }
}
