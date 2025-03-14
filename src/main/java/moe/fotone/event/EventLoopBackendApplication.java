package moe.fotone.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EventLoopBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventLoopBackendApplication.class, args);
    }

}
