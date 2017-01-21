package com.skunkworks;

import com.skunkworks.persistence.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * stole on 11.12.16.
 */
@SpringBootApplication
@Slf4j
public class FastOrmApplication {
    //private static final int ITERATIONS = 100_000_000;
    private static final int ITERATIONS = 10_000_000;
//    private static final int ITERATIONS = 1_000;

    @Bean
    CustomerRepository customerRepository() {
        return new CustomerRepository();
    }

    public static void main(String[] args) {
        SpringApplication.run(FastOrmApplication.class, args);
    }

    @Bean
    public CommandLineRunner start(CustomerRepository customerRepository, CustomerRepo customerRepo) {
        return (args) -> {
            log.info("Done" + customerRepository);
            log.info("Done" + customerRepo.findByLastName("sds"));
        };
    }
}
