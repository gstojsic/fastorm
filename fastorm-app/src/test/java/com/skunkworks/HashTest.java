package com.skunkworks;

import lombok.Value;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashTest {

    @Test
    public void hash() throws Exception {
        int iterations = 1_000_000;

        List<HashKey> keySeeds = new ArrayList<>(iterations);
        for (int i = 0; i < iterations; i++) {
            keySeeds.add(new HashKey("1234_" + i, iterations + i, i));
        }

        System.gc();
        HashMap<String, HashKey> stringsToHashKey = Utils.measureTime(
                () -> {
                    HashMap<String, HashKey> stringMap = new HashMap<>(iterations);
                    for (HashKey seed : keySeeds) {
                        String key = seed.getDestination() + seed.getSequenceId() + seed.getGatewayId();
                        stringMap.put(key, seed);
                    }
                    return stringMap;
                },
                "strings insert"
        );

        System.gc();
        HashMap<HashKey, HashKey> values = Utils.measureTime(
                () -> {
                    HashMap<HashKey, HashKey> valueMap = new HashMap<>(iterations);
                    for (HashKey seed : keySeeds) {
                        HashKey key = new HashKey(seed.getDestination(), seed.getSequenceId(), seed.getGatewayId());
                        valueMap.put(key, seed);
                    }
                    return valueMap;
                },
                "values insert"
        );

        System.gc();
        Utils.measureTime(
                () -> {
                    List<HashKey> hashKeys = new ArrayList<>(iterations);
                    for (HashKey seed : keySeeds) {
                        String key = seed.getDestination() + seed.getSequenceId() + seed.getGatewayId();
                        hashKeys.add(stringsToHashKey.get(key));
                    }
                    return hashKeys;
                },
                "strings get"
        );

        System.gc();
        Utils.measureTime(
                () -> {
                    List<HashKey> hashKeys = new ArrayList<>(iterations);
                    for (HashKey seed : keySeeds) {
                        HashKey key = new HashKey(seed.getDestination(), seed.getSequenceId(), seed.getGatewayId());
                        hashKeys.add(values.get(key));
                    }
                    return hashKeys;
                },
                "values get"
        );
    }

    @Value
    private static class HashKey {
        String destination;
        long sequenceId;
        int gatewayId;
    }
}
