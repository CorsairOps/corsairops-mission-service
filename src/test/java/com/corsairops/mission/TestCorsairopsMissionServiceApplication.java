package com.corsairops.mission;

import org.springframework.boot.SpringApplication;

public class TestCorsairopsMissionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(CorsairopsMissionServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}