package com.dut2.memorise.game.events;

public interface IChange {
    void onChangeLivesListener(byte numberOfLives);
    void onChangePointsListener(float numberOfPoints);
    void onChangeLevelsListener(byte levelNumber);
}
