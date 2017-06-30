package com.skunkworks;

import com.skunkworks.persistence.Customer;
import com.skunkworks.persistence.CustomerCache;
import com.skunkworks.persistence.CustomerCacheImpl;
import com.skunkworks.persistence.CustomerDaoImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Function;

/**
 * stole on 11.12.16.
 */
@SpringBootApplication
@Slf4j
public class FastOrmApplication {
    private static final int ITERATIONS = 1_000_000;

    @Bean
    CustomerDaoImpl customerDao(DataSource dataSource) {
        return new CustomerDaoImpl(dataSource);
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource")
    DataSource dataSource() {
        return new DriverManagerDataSource();
    }

    public static void main(String[] args) {
        SpringApplication.run(FastOrmApplication.class, args);
    }

    @Bean
    public CommandLineRunner start(
            final CustomerDaoImpl customerDao,
            final CustomerRepo customerRepo,
            final DataSource dataSource
    ) {
        return (args) -> {
//            customerRepo.deleteAll();
//            List<Customer> customers = new ArrayList<>(ITERATIONS);
//            for (long i = 1; i <= ITERATIONS; i++) {
//                Customer customer = new Customer();
//                customer.setId(i);
//                customer.setFirstName("Ivo" + i);
//                customer.setLastName("Ivic" + i);
//                customers.add(customer);
//            }
//            customerRepo.save(customers);
//
            //loadTest(dataSource);

            List<Customer> customers = customerDao.findByFirstNameAndLastName("Ivo22", "Ivic22");
            if (customers != null) {
                log.info("customers size:" + customers.size());
            }


            List<Customer> loadedFastOrm = measureTime(t -> {
                try {
                    return customerDao.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            Iterable<Customer> loadedSpring = measureTime(t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

            loadedFastOrm = measureTime(t -> {
                try {
                    return customerDao.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            loadedSpring = measureTime(t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

            loadedFastOrm = measureTime(t -> {
                try {
                    return customerDao.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            loadedSpring = measureTime(t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

            loadedFastOrm = measureTime(t -> {
                try {
                    return customerDao.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            loadedSpring = measureTime(t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

            loadedFastOrm = measureTime(t -> {
                try {
                    return customerDao.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            loadedSpring = measureTime(t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

            CustomerCache customerCache = new CustomerCacheImpl(customerDao);
            Customer customer = customerCache.findByFirstName("Ivo233232");
            if (customer != null) {
                log.info("Customer found:" + customer);
            } else {
                log.info("Customer not found");
            }

            log.info("Done");
        };
    }

    private <T, R> R measureTime(Function<T, R> function, String message) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        R result = function.apply(null);
        stopWatch.stop();
        log.info(message + ":" + stopWatch.getTime());
        return result;
    }
}
