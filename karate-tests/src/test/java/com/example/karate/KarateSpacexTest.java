package com.example.karate;

import com.intuit.karate.junit5.Karate;

class KarateSpacexTest {
    @Karate.Test
    Karate launches() {
        return Karate.run("classpath:karate/spacex-launches.feature");
    }
}

