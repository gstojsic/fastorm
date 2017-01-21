package com.skunkworks.serialization;

import com.skunkworks.serialization.annotation.JsonSerializable;
import lombok.Data;

import java.beans.Transient;

/**
 * stole on 11.12.16.
 */
@JsonSerializable
@Data
public class BagOfPrimitives {
    public static final long DEFAULT_VALUE = 0;
    private long longValue;
    private int intValue;
    private boolean booleanValue;
    private String nullValue;
    private String stringValue;

    public BagOfPrimitives() {
        this(DEFAULT_VALUE, 0, false, "");
    }

    public BagOfPrimitives(long longValue, int intValue, boolean booleanValue, String stringValue) {
        this.longValue = longValue;
        this.intValue = intValue;
        this.booleanValue = booleanValue;
        this.nullValue = null;
        this.stringValue = stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    @Transient
    public String getExpectedJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"longValue\":").append(longValue).append(",");
        sb.append("\"intValue\":").append(intValue).append(",");
        sb.append("\"booleanValue\":").append(booleanValue).append(",");
        sb.append("\"stringValue\":\"").append(stringValue).append("\"");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (booleanValue ? 1231 : 1237);
        result = prime * result + intValue;
        result = prime * result + (int) (longValue ^ (longValue >>> 32));
        result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BagOfPrimitives other = (BagOfPrimitives) obj;
        if (booleanValue != other.booleanValue)
            return false;
        if (intValue != other.intValue)
            return false;
        if (longValue != other.longValue)
            return false;
        if (stringValue == null) {
            if (other.stringValue != null)
                return false;
        } else if (!stringValue.equals(other.stringValue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("(longValue=%d,intValue=%d,booleanValue=%b,stringValue=%s)",
                longValue, intValue, booleanValue, stringValue);
    }
}
