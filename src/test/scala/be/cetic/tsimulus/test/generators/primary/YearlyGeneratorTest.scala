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

package be.cetic.tsimulus.test.generators.primary

import be.cetic.tsimulus.config.GeneratorFormat
import org.scalatest.{FlatSpec, Matchers}
import spray.json._
import be.cetic.tsimulus.generators.primary.YearlyGenerator

class YearlyGeneratorTest extends FlatSpec with Matchers
{
   val source =
      """
        |{
        |   "name": "yearly-generator",
        |   "type": "yearly",
        |   "points": {"2015": 42.12, "2016": 13.37, "2017": 6.022}
        |}
        |
      """.stripMargin

   "A yearly generator" should "be correctly read from a json document" in {
      val generator = YearlyGenerator(source.parseJson)

      generator.name shouldBe Some("yearly-generator")
      generator.`type` shouldBe "yearly"
      generator.points shouldBe Map(2015 -> 42.12, 2016 -> 13.37, 2017 -> 6.022)
   }

   it should "be correctly extracted from the global extractor" in {
      noException should be thrownBy GeneratorFormat.read(source.parseJson)
   }

   it should "be correctly exported to a json document" in {
      val generator = new YearlyGenerator(Some("yearly-generator"), Map(
         2015 -> 42.12,
         2016 -> 13.37,
         2017 -> 6.022)
      )

      generator shouldBe YearlyGenerator(generator.toJson)
   }
}
