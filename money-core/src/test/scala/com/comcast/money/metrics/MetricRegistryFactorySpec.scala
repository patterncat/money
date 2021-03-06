/*
 * Copyright 2012-2015 Comcast Cable Communications Management, LLC
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

package com.comcast.money.metrics

import com.codahale.metrics.MetricRegistry
import com.typesafe.config.Config
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest._
import Matchers._

object MockMetricRegistryFactory extends MetricRegistryFactory with MockitoSugar {
  lazy val mockRegistry = mock[MetricRegistry]

  def metricRegistry(config: Config): MetricRegistry = ???

}
class MockMetricRegistryFactory extends MetricRegistryFactory {
  def metricRegistry(config: Config): MetricRegistry = MockMetricRegistryFactory.mockRegistry
}

class MetricRegistryExtensionSpec extends WordSpecLike with BeforeAndAfter with MockitoSugar {

  private val conf = mock[Config]

  "The MetricRegistry" when {
    "use the DefaultMetricRegistryFactory" should {
      "creating MetricRegistries" in {

        doReturn("com.comcast.money.metrics.DefaultMetricRegistryFactory").when(conf).getString("metrics-registry.class-name")

        val registry = new MetricRegistryExtensionImpl(conf).metricRegistry

        registry shouldNot be(null)
      }
    }

    "fall back to the DefaultMetricRegistryFactory" should {
      "when the config is broken" in {

        doReturn("lorem ipsum").when(conf).getString("metrics-registry.class-name")

        intercept[ClassNotFoundException] {
          val registry = new MetricRegistryExtensionImpl(conf).metricRegistry
        }
      }
    }

    "use the MockMetricRegistryFactory" should {
      "when configured so" in {
        doReturn("com.comcast.money.metrics.MockMetricRegistryFactory").when(conf).getString("metrics-registry.class-name")

        val ext = new MetricRegistryExtensionImpl(conf)
        val registry1 = ext.metricRegistry
        val registry2 = ext.metricRegistry

        registry1 should be theSameInstanceAs registry2
      }
    }
  }
}
