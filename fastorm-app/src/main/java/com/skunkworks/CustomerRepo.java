package com.skunkworks;

import com.skunkworks.persistence.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * stole on 21.01.17.
 */
public interface CustomerRepo extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);
}
