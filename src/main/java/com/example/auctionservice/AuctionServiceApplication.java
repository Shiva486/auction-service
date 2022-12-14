package com.example.auctionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.example.auctionservice" })
@EnableJpaRepositories(basePackages = "com.example.auctionservice.repository")
public class AuctionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionServiceApplication.class, args);
    }

}
