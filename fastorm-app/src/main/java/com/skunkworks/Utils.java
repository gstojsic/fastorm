package com.skunkworks;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * stole on 14.07.17.
 */
@Slf4j
public enum Utils {
    ;

    public static <R> R measureTime(Supplier<R> supplier, String message) throws Exception {
        return measureTime(supplier, message, null);
    }

    public static <R> R measureTime(Supplier<R> supplier, String message, AtomicLong sumTime) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        R result = supplier.get();
        stopWatch.stop();
        long time = stopWatch.getTime();
        log.info(message + ":" + time);
        if (sumTime != null) {
            sumTime.addAndGet(time);
        }
        return result;
    }

}
