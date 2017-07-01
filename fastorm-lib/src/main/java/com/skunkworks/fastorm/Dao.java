package com.skunkworks.fastorm;

import java.io.Serializable;
import java.util.List;

/**
 * stole on 29.01.17.
 */
public interface Dao<T, ID extends Serializable> {

    T save(T entity);

    List<T> save(Iterable<T> entities);

    T findOne(ID id);

    boolean exists(ID id);

    List<T> findAll();

    List<T> findAll(Iterable<ID> ids);

    long count();

    void delete(ID id);

    void delete(T entity);

    void delete(Iterable<T> entities);

    void deleteAll();
}
