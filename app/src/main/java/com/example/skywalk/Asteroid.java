package com.example.skywalk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Asteroid {
    private final Bitmap image;
    private int x, y;
    private final float dx;
    private final float dy;
    private final int screenWidth;
    private final int screenHeight;

    public Asteroid(Bitmap bmp, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Taille aléatoire entre 3% et 6% de la largeur d'écran
        Random rand = new Random();
        int baseSize = (int)(screenWidth * (0.03 + rand.nextFloat() * 0.03));
        image = Bitmap.createScaledBitmap(bmp, baseSize, baseSize, true);

        // Position initiale aléatoire sur les bords
        if(rand.nextBoolean()) {
            x = rand.nextInt(screenWidth);
            y = rand.nextBoolean() ? 0 : screenHeight;
        } else {
            x = rand.nextBoolean() ? 0 : screenWidth;
            y = rand.nextInt(screenHeight);
        }
        // Direction aléatoire
        dx = (rand.nextFloat() - 0.5f) * 5;
        dy = (rand.nextFloat() - 0.5f) * 5;
    }

    public void update() {
        x += (int) dx;
        y += (int) dy;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }

    public boolean isOutOfScreen() {
        return x < -image.getWidth() || x > this.screenWidth + image.getWidth() ||
                y < -image.getHeight() || y > this.screenHeight + image.getHeight();
    }

    public Rect getCollisionRect() {
        return new Rect(x, y, x + image.getWidth(), y + image.getHeight());
    }
}