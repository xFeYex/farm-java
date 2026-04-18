package org.trpg.farming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FarmingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmingApplication.class, args);
    }

}
