package com.dut2.memorise.game.events;

public interface ITimer {
    void onTimerInit();
    void onTimerChange(long time);
    void onTimerTimeout();
    void onTimerFinish();
}
