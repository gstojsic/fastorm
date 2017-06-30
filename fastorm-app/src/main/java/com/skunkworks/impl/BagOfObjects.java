package com.skunkworks.impl;

/**
 * stole on 30.06.17.
 */
public class BagOfObjects {
    int theInt;
    Integer theInteger;
    boolean theBoolean;
    Boolean theBool;
    long theLong;
    Long theLongNull;
    String theString;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BagOfObjects that = (BagOfObjects) o;

        if (theInt != that.theInt) return false;
        if (theBoolean != that.theBoolean) return false;
        if (theLong != that.theLong) return false;
        if (theInteger != null ? !theInteger.equals(that.theInteger) : that.theInteger != null) return false;
        if (theBool != null ? !theBool.equals(that.theBool) : that.theBool != null) return false;
        if (theLongNull != null ? !theLongNull.equals(that.theLongNull) : that.theLongNull != null) return false;
        return theString != null ? theString.equals(that.theString) : that.theString == null;
    }

    @Override
    public int hashCode() {
        int result = theInt;
        result = 31 * result + (theInteger != null ? theInteger.hashCode() : 0);
        result = 31 * result + (theBoolean ? 1 : 0);
        result = 31 * result + (theBool != null ? theBool.hashCode() : 0);
        result = 31 * result + (int) (theLong ^ (theLong >>> 32));
        result = 31 * result + (theLongNull != null ? theLongNull.hashCode() : 0);
        result = 31 * result + (theString != null ? theString.hashCode() : 0);
        return result;
    }
}
