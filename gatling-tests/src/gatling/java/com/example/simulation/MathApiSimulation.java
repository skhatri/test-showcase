package com.example.simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

public class MathApiSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:32108")
        .acceptHeader("application/json")
        .userAgentHeader("Gatling Performance Test");

    ScenarioBuilder addScenario = scenario("Add Numbers Test")
        .exec(
            http("Add Request")
                .get("/add?num1=10&num2=20")
                .check(status().is(200))
                .check(jsonPath("$.result").ofDouble().is(30.0))
        );

    ScenarioBuilder multiplyScenario = scenario("Multiply Numbers Test")
        .exec(
            http("Multiply Request")
                .get("/multiply?num1=5&num2=6")
                .check(status().is(200))
                .check(jsonPath("$.result").ofDouble().is(30.0))
        );

    FeederBuilder<String> numberFeeder = csv("numbers.csv").circular();

    ScenarioBuilder mixedScenario = scenario("Mixed Operations Test")
        .feed(numberFeeder)
        .exec(
            http("Add Random")
                .get("/add?num1=#{num1}&num2=#{num2}")
                .check(status().is(200))
        )
        .pause(1)
        .exec(
            http("Multiply Random")
                .get("/multiply?num1=#{num1}&num2=#{num2}")
                .check(status().is(200))
        );

    {
        setUp(
            addScenario.injectOpen(
                rampUsersPerSec(1).to(10).during(Duration.ofSeconds(30)),
                constantUsersPerSec(10).during(Duration.ofSeconds(60))
            ),
            multiplyScenario.injectOpen(
                rampUsersPerSec(1).to(10).during(Duration.ofSeconds(30)),
                constantUsersPerSec(10).during(Duration.ofSeconds(60))
            ),
            mixedScenario.injectOpen(
                constantUsersPerSec(5).during(Duration.ofSeconds(90))
            )
        ).protocols(httpProtocol)
         .assertions(
             global().responseTime().max().lt(500),
             global().successfulRequests().percent().gt(95.0)
         );
    }
}
