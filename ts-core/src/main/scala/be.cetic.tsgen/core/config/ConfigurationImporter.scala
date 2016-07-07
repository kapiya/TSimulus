package be.cetic.tsgen.core.config

import java.io.File

import com.github.nscala_time.time.Imports._
import spray.json._
import DefaultJsonProtocol._
import be.cetic.tsgen.core.Main

import scala.io.Source
import be.cetic.tsgen.core.config.GeneratorLeafFormat._

import scala.annotation.tailrec
import scala.util.Random

/**
  * An importer of a configuration from a JSON document.
  */
object ConfigurationImporter
{
   val dtf = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss")


   def main(args: Array[String])
   {
      val file = new File("XXX")

      // val document = Source.fromFile(file).getLines().mkString("").parseJson

      val document =
         """
           |{
           |   "generators": [
           |      {
           |         "name": "daily-generator",
           |         "type": "daily",
           |         "points": {"10:00:00.000": 12, "17:00:00.000": 15, "20:00:00.000": 12}
           |      },
           |      {
           |         "name": "noisy-daily",
           |         "type": "aggregate",
           |         "aggregator": "sum",
           |         "generators": [
           |            "daily-generator",
           |            {
           |                "type": "arma",
           |                "model": { "std": 0.1, "c": 0, "seed": 159357},
           |                "timestep": 3600000
           |            }
           |         ]
           |      },
           |      {
           |         "name":  "partial-daily",
           |         "type": "partial",
           |         "generator": "daily-generator",
           |         "from": "2016-01-01 00:00:00.000",
           |         "to": "2017-01-01 00:00:00.000"
           |      }
           |   ],
           |   "exported": [
           |      {
           |         "name": "series-A",
           |         "generator": "daily-generator",
           |         "frequency": 300000
           |      },
           |      {
           |         "name": "series-B",
           |         "generator": "noisy-daily",
           |         "frequency": 300000
           |      }
           |   ],
           |   "from": "2016-01-01 00:00:00.000",
           |   "to": "2016-01-07 00:00:00.000"
           |}
         """.stripMargin.parseJson


   }
}