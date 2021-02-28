package com.dut2.memorise.game.thread;

import com.dut2.memorise.game.view.Block;

public class BlockThread extends Thread{
    private final long TIMER;
    private final Block block;

    public BlockThread(Block block, long TIMER){
        this.TIMER = TIMER;
        this.block = block;
    }

    @Override
    public void run() {
        try {
            block.setPressed(true);
            Thread.sleep(TIMER);
            block.setPressed(false);
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
