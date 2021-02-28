package com.dut2.memorise.game.events;

public interface IEngine {
    void onInitLevel();
    void onLoadBlock(byte pos);
    void onLoadLightenBlock(byte pos);
    long onBeforeLevelStart();
    void onStartLevel();
    Runnable onLightenBlocksFinished();
    void onEndLevel(boolean levelWon, byte levelCount);
    void onEndGame(boolean gameWon, float points);
    void onReset(byte resetState);
}