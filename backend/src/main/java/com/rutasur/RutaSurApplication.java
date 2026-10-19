package com.rutasur;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication @EnableScheduling
public class RutaSurApplication {

    public static void main(String[] args) {
        SpringApplication.run(RutaSurApplication.class, args);
    }
}
