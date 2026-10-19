package com.rutasur.config;

import org.springframework.context.annotation.*;
import java.time.Clock;
import java.time.ZoneId;

@Configuration public class AppConfig {
    @Bean public Clock clock() {
        return Clock.system(ZoneId.of("America/Bogota"));
    }
}
