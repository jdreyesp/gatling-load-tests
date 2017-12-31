/**
 * Copyright 2011-2017 GatlingCorp (http://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jdreyesp.examples.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class FeederSimulation extends Simulation {

  val basicSimulation = new BasicSimulation()

  val feeder = csv("search.csv").random

  object Search {

    val search = exec(http("Home")
      .get("/"))
      .pause(1)
      .feed(feeder) // 3
      .exec(http("Search")
      .get("/computers?f=${searchCriterion}") // 4
      .check(css("a:contains('${searchComputerName}')", "href").saveAs("computerURL"))) // 5
      .pause(1)
      .exec(http("Select")
        .get("${computerURL}")) // 6
      .pause(1)
  }

  val userScenario = scenario("Normal user scenario")
  .exec(Search.search)    // A scenario is a chain of requests and pauses


  setUp(userScenario.inject(rampUsers(10) over (10 seconds)))
    .protocols(basicSimulation.httpConf)
}
