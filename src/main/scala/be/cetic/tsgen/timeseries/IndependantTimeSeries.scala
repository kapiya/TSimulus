package be.cetic.tsgen.timeseries

import org.joda.time.LocalDateTime

/**
  * A time series generator able to provide each value of the time series independently.
  */
trait IndependantTimeSeries[T] extends TimeSeries[T]
{
   /**
     * @param time A point in the time series
     * @return the value associated to the given time in the time series.
     */
   def compute(time: LocalDateTime): Option[T]

   def compute(times: Stream[LocalDateTime]) = times.map(t => (t, compute(t)))
}