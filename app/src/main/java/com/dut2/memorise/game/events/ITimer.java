package com.dut2.memorise.game.events;

public interface ITimer {
    void onTimerInit(long time);
    void onTimerChange(long time);
    void onTimerTimeout();
    void onTimerFinish();
}
