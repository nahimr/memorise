package com.dut2.memorise.game.modes;

import com.dut2.memorise.game.Engine;

public class Timer extends Engine {
    public Timer(byte minLightenBlock, byte maxLives, float weight, boolean timer) {
        super((byte) 1,(byte) 3,2.0f,true);
    }

}