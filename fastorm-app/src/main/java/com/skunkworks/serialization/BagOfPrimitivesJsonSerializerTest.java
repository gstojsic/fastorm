package com.skunkworks.serialization;

/**
 * stole on 11.12.16.
 */
public final class BagOfPrimitivesJsonSerializerTest {
    public static String toJson(BagOfPrimitives bag) {
        return "{" + "\"longValue\":" + bag.getLongValue() + ",\"intValue\":" + bag.getIntValue() + ",\"booleanValue\":" + bag.isBooleanValue() + ",\"stringValue\":\"" + bag.getStringValue() + "\"}";
    }
}
