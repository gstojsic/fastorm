package com.skunkworks.persistence;

import com.skunkworks.fastorm.annotations.Cache;

import java.util.List;

/**
 * stole on 09.06.17.
 */
@Cache(Customer.class)
interface CustomerCache {

    Customer findByFirstName(String firstName);

    List<Customer> findByLastName(String lastName);
}