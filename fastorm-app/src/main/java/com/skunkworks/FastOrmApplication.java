package com.skunkworks;

import com.skunkworks.fastorm.annotations.GenerateFastOrmConfig;
import com.skunkworks.persistence.cache.CustomerCache;
import com.skunkworks.persistence.dao.CustomerDaoGenerated;
import com.skunkworks.persistence.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * stole on 11.12.16.
 */
@SpringBootApplication
@Slf4j
@GenerateFastOrmConfig
//@Import(com.skunkworks.FastOrmConfig.class)
public class FastOrmApplication {
    private static final int ITERATIONS = 1_000_000;
    private static final int TEST_ITERATIONS = 6;
    private static final int WARMUP_ITERATIONS = 2;

//    @Bean
//    CustomerDaoGenerated customerDao(DataSource dataSource) {
//        return new CustomerDaoGenerated(dataSource);
//    }

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
            final CustomerDaoGenerated customerDao,
            final CustomerRepo customerRepo,
            final CustomerCache customerCache,
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

            List<Customer> loadedFastOrm = null;
            Iterable<Customer> loadedSpring = null;
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                customerDao.findAll();
                customerRepo.findAll();
            }

            AtomicLong sumFastOrmTime = new AtomicLong(0);
            AtomicLong sumSpringTime = new AtomicLong(0);
            for (int i = 0; i < TEST_ITERATIONS; i++) {
                System.gc();
                loadedFastOrm = measureTime(t -> {
                            try {
                                return customerDao.findAll();
                            } catch (Exception e) {
                                return null;
                            }
                        },
                        "LoadAll fastOrm",
                        sumFastOrmTime
                );

                System.gc();
                loadedSpring = measureTime(t -> {
                            try {
                                return customerRepo.findAll();
                            } catch (Exception e) {
                                return null;
                            }
                        },
                        "LoadAll spring",
                        sumSpringTime
                );
            }
            double fastOrmAverage = (.0D + sumFastOrmTime.get()) / TEST_ITERATIONS;
            double springAverage = (.0D + sumSpringTime.get()) / TEST_ITERATIONS;

            log.info("Average fastOrm:" + fastOrmAverage);
            log.info("Average spring:" + springAverage);
            log.info("Ratio (spring/fastOrm):" + (springAverage / fastOrmAverage));

            //CustomerCache customerCache = new CustomerCacheGenerated(customerDao);
            Customer customer = customerCache.findByFirstName("Ivo233232");
            if (customer != null) {
                log.info("Customer found:" + customer);
            } else {
                log.info("Customer not found");
            }

            log.info("Done");
        };
    }

    private <T, R> R measureTime(Function<T, R> function, String message, AtomicLong sumTime) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        R result = function.apply(null);
        stopWatch.stop();
        long time = stopWatch.getTime();
        log.info(message + ":" + time);
        sumTime.addAndGet(time);
        return result;
    }
}
