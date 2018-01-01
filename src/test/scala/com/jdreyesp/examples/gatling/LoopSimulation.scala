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

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class LoopSimulation extends Simulation {

  object Create {

    //Create some computers
    //This will generate random 200 / 201. The check will fail randomly with 201 responses
    val create = exec(http("Form")
      .get("/computers/new"))
      .pause(1)
      .exec(http("Post")
        .post("/computers")
        .check(status.is(session => 200 + ThreadLocalRandom.current.nextInt(2))))
  }

  //We define a tryMaxExecution, capturing the error in 201 cases
  val tryMaxExecution = tryMax(2) {
    exec(Create.create)
  }.exitHereIfFailed

  //We now create the admin scenario
  val adminScenario = scenario("Admin user scenario")
  .exec(tryMaxExecution)

  //Set up the scenario
  setUp(adminScenario.inject(rampUsers(10) over (10 seconds)))
    .protocols(BasicSimulation.httpConf)

}
