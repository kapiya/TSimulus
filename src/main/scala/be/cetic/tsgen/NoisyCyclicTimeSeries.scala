package be.cetic.tsgen

import org.joda.time.{DateTimeZone, Duration, LocalDateTime}

/**
  * This time series generator combines a time series generator with a randomization process represented
  * by an ARMA model.
  *
  * Discrete values from the noise generator are linearly interpolated.
  *
  * @param generator The generator being composited in this class.
  * @param noise     The noise generator used to introduce pseudo-random values in the generated time series
  * @param origin    The position 0 of the time series. Usefull when the time series values are generated by a random
  *                  walk, which is typically the case with the ARMA model used as noise generator.
  * @param timeStep  The time interval between two steps of the noise generator.
  */
case class NoisyCyclicTimeSeries(generator: ScalarTimeSeriesGenerator,
                                 noise: ARMA,
                                 origin: LocalDateTime,
                                 timeStep: Duration) extends ScalarTimeSeriesGenerator
{
   override def compute(times: Stream[LocalDateTime]): Stream[Double] = (generator.compute(times) zip computeNoise(times)).map(e => e._1 + e._2)

   def compute(time: LocalDateTime): Double = generator.compute(time) + computeNoise(time)

   // TODO: provide a more efficient (O(n) instead of O(n^2)) way to compute this function by reusing steps in the random walk.
   private def computeNoise(times: Stream[LocalDateTime]): Stream[Double] = times.map(computeNoise)

   private def computeNoise(time: LocalDateTime): Double =
   {
      def discreteTimeLine(previous: LocalDateTime, increment: Boolean): Stream[LocalDateTime] =
      {
         val next = if(increment) previous plus timeStep
                    else previous minus timeStep

         next #:: discreteTimeLine(next, increment)
      }

      val timeLine = discreteTimeLine(origin, time isAfter origin)
      val progress = timeLine zip noise.series

      val values = takeUntil(progress, {p: (LocalDateTime, Double) => (origin compareTo time) == (p._1 compareTo time)})
         .takeRight(2)
         .toList

      if(values.size == 1) return values.head._2

      val t1 = values.head._1.toDateTime(DateTimeZone.UTC)
      val t2 = values.last._1.toDateTime(DateTimeZone.UTC)

      assert((t1 isBefore time.toDateTime(DateTimeZone.UTC)) || (t1 equals time.toDateTime(DateTimeZone.UTC)))
      assert((t2 isAfter time.toDateTime(DateTimeZone.UTC)) || (t2 equals time.toDateTime(DateTimeZone.UTC)))

      val v1 = values.head._2
      val v2 = values.last._2

      val deltaT = new Duration(t1, t2).getMillis
      val deltaV = v2 - v1

      val ratio = new Duration(t1, time.toDateTime(DateTimeZone.UTC)).getMillis / deltaT.toDouble
      assert(ratio >= 0, "ratio error: " + ratio)

      val value = v1 + (ratio * deltaV)

      return value
   }

   private def takeUntil[T](xs: Stream[T], predicate: T => Boolean): Stream[T] =
   {
      if(predicate(xs.head)) xs.head #:: takeUntil(xs.tail, predicate)
      else xs.head #:: Stream.empty
   }
}