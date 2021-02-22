package com.dut2.memorise.game.events;

public interface ILevel{
    void onLevelStarted();
    void onLevelFinished(boolean isPlayerWon);
}