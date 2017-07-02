package com.skunkworks.persistence.cache;

import com.skunkworks.fastorm.annotations.Cache;
import com.skunkworks.persistence.entity.Person;

/**
 * stole on 02.07.17.
 */
@Cache(Person.class)
public interface PersonCache {

    Person findById(Long id);

    Person findByLastNameAndMarried(String lastName, Boolean married);
}
