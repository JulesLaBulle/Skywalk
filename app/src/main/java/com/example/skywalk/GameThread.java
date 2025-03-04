package com.example.skywalk;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private boolean running;

    public GameThread(SurfaceHolder holder, GameView gameView) {
        this.surfaceHolder = holder;
        this.gameView = gameView;
    }

    @Override
    public void run() {
        long startTime;
        long waitTime;
        int targetFPS = 60;
        long targetTime = 1000 / targetFPS;

        while(running) {
            startTime = System.nanoTime();

            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized(surfaceHolder) {
                    gameView.update();
                    gameView.draw(canvas);
                }
            } finally {
                if(canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            waitTime = targetTime - (System.nanoTime() - startTime)/1000000;
            try {
                if(waitTime > 0) {
                    sleep(waitTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}