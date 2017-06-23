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
    private final Map<String, Customer> firstNameIndex = new HashMap<>();
    private final Map<String, List<Customer>> lastNameIndex = new HashMap<>();
    private final Map<FirstNameAndLastNameKey, Customer> firstNameAndlastNameIndex = new HashMap<>();

    public CustomerCacheManual(Dao<Customer, Long> dao) {
        this.dao = dao;
        loadData();
    }

    private void loadData() {
        List<Customer> entities = dao.findAll();
        for (Customer entity : entities) {
            firstNameIndex.put(entity.getFirstName(), entity);
            lastNameIndex.computeIfAbsent(entity.getLastName(), s -> new ArrayList<>()).add(entity);

            firstNameAndlastNameIndex.put(new FirstNameAndLastNameKey(entity.getFirstName(), entity.getLastName()), entity);
        }
    }

    @Override
    public Customer findByFirstName(String firstName) {
        return firstNameIndex.get(firstName);
    }

    @Override
    public List<Customer> findByLastName(String lastName) {
        return lastNameIndex.get(lastName);
    }

    @Override
    public Customer findByFirstNameAndLastName(String firstName, String lastName) {
        return firstNameAndlastNameIndex.get(new FirstNameAndLastNameKey(firstName, lastName));
    }

    private static final class FirstNameAndLastNameKey {
        final String firstName;
        final String lastName;

        FirstNameAndLastNameKey(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FirstNameAndLastNameKey that = (FirstNameAndLastNameKey) o;

            if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
            return lastName != null ? lastName.equals(that.lastName) : that.lastName == null;
        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            return result;
        }
    }
}

