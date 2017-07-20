package com.skunkworks;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashTest {

    @Test
    public void hash() throws Exception {
        int iterations = 1_000_000;

        List<HashKey> keySeeds = new ArrayList<>(iterations);
        for (int i = 0; i < iterations; i++) {
            keySeeds.add(new HashKey("1234_" + i, iterations + i, i));
        }

        HashMap<String, HashKey> warmupStrPut = stringHashkeyPut(keySeeds, iterations);
        HashMap<HashKey, HashKey> warmupValuePut = valueHashkeyPut(keySeeds, iterations);

        System.gc();
        HashMap<String, HashKey> stringsToHashKey = Utils.measureTime(
                () -> stringHashkeyPut(keySeeds, iterations),
                "strings insert"
        );

        System.gc();
        HashMap<HashKey, HashKey> values = Utils.measureTime(
                () -> valueHashkeyPut(keySeeds, iterations),
                "values insert"
        );

        List<HashKey> warmupStrGet = stringHashkeyGet(stringsToHashKey, keySeeds, iterations);
        List<HashKey> warmupValueGet = valueHashkeyGet(values, keySeeds, iterations);

        System.gc();
        List<HashKey> strGet = Utils.measureTime(
                () -> stringHashkeyGet(stringsToHashKey, keySeeds, iterations),
                "strings get"
        );

        System.gc();
        List<HashKey> valuesGet = Utils.measureTime(
                () -> valueHashkeyGet(values, keySeeds, iterations),
                "values get"
        );

        Set<HashKey> strGetSet = new HashSet<>(strGet);
        Set<HashKey> valueGetSet = new HashSet<>(valuesGet);

        assertTrue("Arrays match:", strGetSet.containsAll(valueGetSet));

        log.info("warmupStrPut:" + warmupStrPut.size());
        log.info("warmupValuePut:" + warmupValuePut.size());
        log.info("warmupStrGet:" + warmupStrGet.size());
        log.info("warmupValueGet:" + warmupValueGet.size());
    }

    private HashMap<String, HashKey> stringHashkeyPut(List<HashKey> keySeeds, int iterations) {
        HashMap<String, HashKey> stringMap = new HashMap<>(iterations);
        for (HashKey seed : keySeeds) {
            String key = seed.getDestination() + seed.getSequenceId() + seed.getGatewayId();
            stringMap.put(key, seed);
        }
        return stringMap;
    }

    private HashMap<HashKey, HashKey> valueHashkeyPut(List<HashKey> keySeeds, int iterations) {
        HashMap<HashKey, HashKey> valueMap = new HashMap<>(iterations);
        for (HashKey seed : keySeeds) {
            HashKey key = new HashKey(seed.getDestination(), seed.getSequenceId(), seed.getGatewayId());
            valueMap.put(key, seed);
        }
        return valueMap;
    }

    private List<HashKey> stringHashkeyGet(HashMap<String, HashKey> stringsToHashKey, List<HashKey> keySeeds, int iterations) {
        List<HashKey> hashKeys = new ArrayList<>(iterations);
        for (HashKey seed : keySeeds) {
            String key = seed.getDestination() + seed.getSequenceId() + seed.getGatewayId();
            hashKeys.add(stringsToHashKey.get(key));
        }
        return hashKeys;
    }

    private List<HashKey> valueHashkeyGet(HashMap<HashKey, HashKey> values, List<HashKey> keySeeds, int iterations) {
        List<HashKey> hashKeys = new ArrayList<>(iterations);
        for (HashKey seed : keySeeds) {
            HashKey key = new HashKey(seed.getDestination(), seed.getSequenceId(), seed.getGatewayId());
            hashKeys.add(values.get(key));
        }
        return hashKeys;
    }

    @Value
    private static class HashKey {
        String destination;
        long sequenceId;
        int gatewayId;
    }
}
