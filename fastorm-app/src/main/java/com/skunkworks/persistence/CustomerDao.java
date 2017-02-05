package com.skunkworks.persistence;

import com.skunkworks.fastorm.annotations.Dao;

import java.util.List;

/**
 * stole on 28.01.17.
 */
@Dao(Customer.class)
public interface CustomerDao {

    Customer findByFirstName(String firstName);

    List<Customer> findByLastName(String lastName);

    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);
}
