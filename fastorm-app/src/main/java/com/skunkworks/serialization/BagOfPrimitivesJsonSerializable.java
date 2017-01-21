package com.skunkworks.serialization;

/**
 * stole on 12.12.16.
 */
public interface BagOfPrimitivesJsonSerializable extends JsonSerializable {
    @Override
    default String toJson() {
        return "M";
    }
}
