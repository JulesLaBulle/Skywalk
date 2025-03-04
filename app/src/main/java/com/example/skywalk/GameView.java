package com.example.skywalk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap background;
    private GameThread thread;
    private PlayerShip player;
    private final List<Asteroid> asteroids = new ArrayList<>();
    private boolean isGameOver = false;

    // Constructeur utilisé lors de l'inflation depuis XML
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Autres constructeurs recommandés
    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.etoilefond);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // Initialiser le joueur ici, avant de démarrer le thread
        player = new PlayerShip(
                BitmapFactory.decodeResource(getResources(), R.drawable.tie),
                getWidth(),
                getHeight(),
                getWidth() / 2,
                getHeight() / 2
        );

        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        // Réinitialiser le joueur avec les nouvelles dimensions
        player = new PlayerShip(
                BitmapFactory.decodeResource(getResources(), R.drawable.tie),
                width,
                height,
                width /2,
                height /2
        );
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        thread.setRunning(false);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (!isGameOver && player != null) {
            player.update();
            updateAsteroids();
            checkCollisions();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(canvas != null) {
            // Dessiner le fond à partir du coin supérieur gauche
            canvas.drawBitmap(background, 0, 0, null);
            // Dessiner le joueur et les astéroïdes par-dessus
            player.draw(canvas);
            for (Asteroid asteroid : asteroids) {
                asteroid.draw(canvas);
            }
        }
    }

    private void updateAsteroids() {
        // Ajout aléatoire d'astéroïdes
        if(Math.random() < 0.2) {
            asteroids.add(new Asteroid(
                    BitmapFactory.decodeResource(getResources(), getRandomAsteroid()),
                    getWidth(),
                    getHeight()
            ));
        }

        // Mise à jour position
        Iterator<Asteroid> it = asteroids.iterator();
        while(it.hasNext()) {
            Asteroid a = it.next();
            if(a.isOutOfScreen()) {
                it.remove();
            } else {
                a.update();
            }
        }
    }

    private int getRandomAsteroid() {
        int[] asteroids = {
                R.drawable.asteroid1,
                R.drawable.asteroid2,
                R.drawable.asteroid3,
                R.drawable.asteroid4
        };
        return asteroids[(int)(Math.random() * 4)];
    }

    private void checkCollisions() {
        Rect playerRect = player.getCollisionRect();
        for(Asteroid asteroid : asteroids) {
            if(Rect.intersects(playerRect, asteroid.getCollisionRect())) {
                isGameOver = true;
                ((GameActivity)getContext()).showGameOver();
                return;
            }
        }
    }

    public void setPlayerMovement(float dx, float dy) {
        player.setMovement(dx, dy);
    }

    public void pause() {
    }

    public void resume() {
    }
}