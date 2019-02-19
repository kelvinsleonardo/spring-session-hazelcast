package com.example.demo;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@SpringBootApplication
@RestController
public class DemoApplication {

    @GetMapping("/session")
    Map<String, Object> session(HttpServletRequest request) {
        final HttpSession session = request.getSession();
        session.setAttribute("count", ofNullable(session.getAttribute("count"))
                .map(count -> (int) count + 1)
                .orElse(1));

        return Map.of("count", session.getAttribute("count"), "id", session.getId());
    }

    @Bean
    CommandLineRunner cmd(HazelcastInstance hz, @Value("${server.port}") int port) {
        return args -> {
            List<Integer> list = hz.getList("my-distributed-list");
            list.add(port);
            list.forEach(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

