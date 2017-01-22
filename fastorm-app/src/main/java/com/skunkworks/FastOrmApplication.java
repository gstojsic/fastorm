package com.skunkworks;

import com.skunkworks.persistence.Customer;
import com.skunkworks.persistence.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * stole on 11.12.16.
 */
@SpringBootApplication
@Slf4j
public class FastOrmApplication {
    private static final int ITERATIONS = 1_000_000;

    @Bean
    CustomerRepository customerRepository(DataSource dataSource) {
        return new CustomerRepository(dataSource);
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
            final CustomerRepository customerRepository,
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

            //loadTest(dataSource);


            List<Customer> loadedFastOrm = measureTime((Function<Void, List<Customer>>) t -> {
                try {
                    return customerRepository.loadAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            Iterable<Customer> loadedSpring = measureTime((Function<Void, Iterable<Customer>>) t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

            measureTime((Function<Void, List<Customer>>) t -> {
                try {
                    return customerRepository.loadAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            measureTime((Function<Void, Iterable<Customer>>) t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

            measureTime((Function<Void, List<Customer>>) t -> {
                try {
                    return customerRepository.loadAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            measureTime((Function<Void, Iterable<Customer>>) t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

            measureTime((Function<Void, List<Customer>>) t -> {
                try {
                    return customerRepository.loadAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll fastOrm");

            measureTime((Function<Void, Iterable<Customer>>) t -> {
                try {
                    return customerRepo.findAll();
                } catch (Exception e) {
                    return null;
                }
            }, "LoadAll spring");

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

//    private List<Customer> loadTest(DataSource dataSource) throws Exception {
//        Connection connection = dataSource.getConnection();
//        //CallableStatement callableStatement = connection.prepareCall("exec dbo.InsertCustomer ?, ?");
//        CallableStatement callableStatement = connection.prepareCall("select * from Customer");
//        ResultSet resultSet = callableStatement.executeQuery();
//        List<Customer> customers = new ArrayList<>();
//        while (resultSet.next()) {
//            Customer customer = new Customer();
//            customer.setId(resultSet.getLong("id"));
//            customer.setFirstName(resultSet.getString("first_name"));
//            customer.setLastName(resultSet.getString("last_name"));
//            customers.add(customer);
//        }
//        return customers;
//    }
}
