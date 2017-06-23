package com.skunkworks.impl;

import com.skunkworks.fastorm.Dao;
import com.skunkworks.persistence.Customer;
import com.skunkworks.persistence.CustomerCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * stole on 14.06.17.
 */
public class CustomerCacheManual implements CustomerCache {

    private final Dao<Customer, Long> dao;
    private final Map<String, Customer> entitiesByFirstName = new HashMap<>();
    private final Map<String, List<Customer>> entitiesByLastName = new HashMap<>();

    public CustomerCacheManual(Dao<Customer, Long> dao) {
        this.dao = dao;
        loadData();
    }

    private void loadData() {
        List<Customer> entities = dao.findAll();
        for (Customer entity : entities) {
            entitiesByFirstName.put(entity.getFirstName(), entity);
            entitiesByLastName.computeIfAbsent(entity.getLastName(), s -> new ArrayList<>()).add(entity);
        }
    }

    @Override
    public Customer findByFirstName(String firstName) {
        return entitiesByFirstName.get(firstName);
    }

    @Override
    public List<Customer> findByLastName(String lastName) {
        return entitiesByLastName.get(lastName);
    }
}

