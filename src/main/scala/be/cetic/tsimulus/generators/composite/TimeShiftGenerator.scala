/*
 * Copyright 2106 Cetic ASBL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.cetic.tsimulus.generators.composite

import be.cetic.tsimulus.config.{GeneratorFormat, Model}
import be.cetic.tsimulus.generators.{Generator, TimeToJson}
import be.cetic.tsimulus.timeseries.composite.TimeShiftTimeSeries
import com.github.nscala_time.time.Imports._
import org.joda.time.Duration
import spray.json.{JsObject, JsString, JsValue, _}

/**
  * A generator for [[be.cetic.tsimulus.timeseries.composite.TimeShiftTimeSeries]].
  */
class TimeShiftGenerator(name: Option[String],
                         val generator: Either[String, Generator[Any]],
                         val shift: Duration) extends Generator[Any](name, "time-shift")
                                              with TimeToJson
{
   override def timeseries(generators: (String) => Generator[Any]) =
   {
      val ts = Model.generator(generators)(generator).timeseries(generators)
      TimeShiftTimeSeries(ts, shift)
   }

   override def toString = "TimeShift(" + name + ", " + generator + ", " + shift.getMillis + ")"

   override def equals(o: Any) = o match {
      case that: TimeShiftGenerator => that.name == this.name && that.shift == this.shift
      case _ => false
   }

   override def toJson: JsValue =
   {
      var t = Map(
         "generator" -> either2json(generator),
         "shift" -> DurationFormat.write(shift),
         "type" -> `type`.toJson
      )

      if(name.isDefined) t = t.updated("name", name.toJson)

      new JsObject(t)
   }

}

object TimeShiftGenerator extends DefaultJsonProtocol with TimeToJson
{
   def apply(value: JsValue): TimeShiftGenerator = {
      val fields = value.asJsObject.fields

      val name = fields.get("name").map(_.convertTo[String])

      val generator = fields("generator") match {
         case JsString(s) => Left(s)
         case g => Right(GeneratorFormat.read(g))
      }

      val shift = fields("shift").convertTo[Duration]

      new TimeShiftGenerator(name, generator, shift)
   }
}
