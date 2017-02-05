package com.skunkworks.persistence;

import com.skunkworks.fastorm.annotations.Dao;

/**
 * stole on 28.01.17.
 */
@Dao(Customer.class)
public interface CustomerDao {

    Customer findByFirstName(String firstName);
}
