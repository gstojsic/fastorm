package com.skunkworks;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.skunkworks.serialization.BagOfPrimitives;
import com.skunkworks.serialization.BagOfPrimitivesJsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;

import java.io.IOException;

/**
 * stole on 11.12.16.
 */
@SpringBootApplication
@Slf4j
public class SerializationApplication {
    //private static final int ITERATIONS = 100_000_000;
    private static final int ITERATIONS = 10_000_000;
//    private static final int ITERATIONS = 1_000;

    public static void main(String[] args) {
        SpringApplication.run(SerializationApplication.class, args);
    }

    @Bean
    public CommandLineRunner start() {
        return (args) -> {
            Gson gson = new Gson();
            BagOfPrimitives bag = new BagOfPrimitives(10L, 15, false, "banana");

            final long start = System.nanoTime();
            for (int i = 1; i <= ITERATIONS; i++) {
                gson.toJson(bag);
            }
            final long end = System.nanoTime();
            log.info("Gson serialization delta:" + (end - start) / 1_000_000_000D);
            String serialized = gson.toJson(bag);
            log.info("Gson serialized:" + serialized);

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            final long startJackson = System.nanoTime();
            for (int i = 1; i <= ITERATIONS; i++) {
                mapper.writeValueAsString(bag);
            }
            final long endJackson = System.nanoTime();
            log.info("Jackson serialization delta:" + (endJackson - startJackson) / 1_000_000_000D);
            log.info("Jackson serialized:" + mapper.writeValueAsString(bag));


            log.info("Marshall serialized:" + BagOfPrimitivesJsonSerializer.toJson(bag));

            final long startMarshall = System.nanoTime();
            for (int i = 1; i <= ITERATIONS; i++) {
                BagOfPrimitivesJsonSerializer.toJson(bag);
            }
            final long endMarshall = System.nanoTime();
            log.info("Marshall serialization delta:" + (endMarshall - startMarshall) / 1_000_000_000D);
            log.info("equals:" + (gson.toJson(bag).equals(BagOfPrimitivesJsonSerializer.toJson(bag))));

            log.info("Homemade serialized:" + toJson(bag));

            long startHomemade = System.nanoTime();
            for (int i = 1; i <= ITERATIONS; i++) {
                toJson(bag);
            }
            final long endHomemade = System.nanoTime();
            log.info("Homemade serialization delta:" + (endHomemade - startHomemade) / 1_000_000_000D);
            log.info("equals:" + (gson.toJson(bag).equals(toJson(bag))));

            final long startDeserialize = System.nanoTime();
            for (int i = 1; i <= ITERATIONS; i++) {
                gson.fromJson(serialized, BagOfPrimitives.class);
            }
            final long endDeserialize = System.nanoTime();
            log.info("Gson deserialization delta:" + (endDeserialize - startDeserialize) / 1_000_000_000D);
        };
    }

//    private String toJson(BagOfPrimitives bag) {
//        try {
//            SegmentedStringWriter sw = new SegmentedStringWriter(new BufferRecycler());
//            sw.append('{');
//            sw.append("\"longValue\":").append(Long.toString(bag.getLongValue()));
//            sw.append(',').append("\"intValue\":").append(Integer.toString(bag.getIntValue()));
//            sw.append(',').append("\"booleanValue\":").append(Boolean.toString(bag.isBooleanValue()));
//            if (bag.getNullValue() != null) {
//                sw.append(',').append("\"nullValue\":\"").append(bag.getNullValue()).append('"');
//            }
//            if (bag.getStringValue() != null) {
//                sw.append(',').append("\"stringValue\":\"").append(bag.getStringValue()).append('"');
//            }
//            sw.append('}');
//            return sw.getAndClear();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private String toJson(BagOfPrimitives bag) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"longValue\":").append(bag.getLongValue());
        sb.append(',').append("\"intValue\":").append(bag.getIntValue());
        sb.append(',').append("\"booleanValue\":").append(bag.isBooleanValue());
        if (bag.getNullValue() != null) {
            sb.append(',').append("\"nullValue\":\"").append(bag.getNullValue()).append('"');
        }
        if (bag.getStringValue() != null) {
            sb.append(',').append("\"stringValue\":\"").append(bag.getStringValue()).append('"');
        }
        sb.append('}');
        return sb.toString();
    }
}
