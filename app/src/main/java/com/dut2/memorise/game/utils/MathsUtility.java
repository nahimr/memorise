package com.dut2.memorise.game.utils;

public final class MathsUtility{
    public static byte mapping(byte t, byte a, byte b, byte c, byte d){
        byte partA = (byte) (d-c);
        byte partB = (byte) (b-a);
        byte divide = (byte) (partA / partB);
        return (byte) (c + (divide* (t-a)));
    }

    public static byte clamp(byte val, byte min, byte max) {
        return (byte) Math.max(min, Math.min(max, val));
    }

}