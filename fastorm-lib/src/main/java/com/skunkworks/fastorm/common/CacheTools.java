package com.skunkworks.fastorm.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * stole on 01.07.17.
 */
public enum CacheTools {
    ;

    public static <T, K> void updateOrAddToListInIndex(final Map<K, List<T>> indexMap, final K key, final T entity) {
        List<T> indexList = indexMap.computeIfAbsent(key, s -> new ArrayList<>());
        int index = indexList.indexOf(entity);
        if (index >= 0) {
            indexList.set(index, entity);
        } else {
            indexList.add(entity);
        }
    }
}
