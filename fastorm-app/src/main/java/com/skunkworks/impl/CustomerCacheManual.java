package com.skunkworks.impl;

import com.skunkworks.fastorm.Dao;
import com.skunkworks.persistence.Customer;
import com.skunkworks.persistence.CustomerCache;

import java.util.List;

/**
 * stole on 14.06.17.
 */
public class CustomerCacheManual implements CustomerCache {

    private final Dao<Customer, Long> dao;
    //private Map<Long, Customer> entitiesById = new HashMap<>();

    public CustomerCacheManual(Dao<Customer, Long> dao) {
        this.dao = dao;
        loadData();
    }

    private void loadData() {
        List<Customer> entities = dao.findAll();
        for (Customer entity : entities) {
        }
    }

    @Override
    public Customer findByFirstName(String firstName) {
        throw new UnsupportedOperationException("findByFirstName");
    }

    @Override
    public List<Customer> findByLastName(String lastName) {
        throw new UnsupportedOperationException("findByLastName");
    }
}

