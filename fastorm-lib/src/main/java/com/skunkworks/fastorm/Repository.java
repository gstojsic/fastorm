package com.skunkworks.fastorm;

import java.util.List;

/**
 * stole on 21.01.17.
 */
public interface Repository<T> {

    //T findById() throws Exception;

    List<T> loadAll() throws Exception;
}
