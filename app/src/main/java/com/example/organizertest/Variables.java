package com.example.organizertest;

public final class Variables {
    private static boolean isInclusive = false;

    public static void setInclusive(boolean isInclusive){
        Variables.isInclusive = isInclusive;
    }
    public static boolean isInclusive(){
        return Variables.isInclusive;
    }
}
