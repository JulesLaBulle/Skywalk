package com.example.skywalk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class PlayerShip {
    private final Bitmap image;
    private int x, y;
    private float dx = 0, dy = 0;
    private final int screenWidth;
    private final int screenHeight;

    public PlayerShip(Bitmap bmp, int screenWidth, int screenHeight, int startX, int startY) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Redimensionner l'image à 5% de la largeur d'écran
        int desiredWidth = (int)(screenWidth * 0.15);
        int desiredHeight = (int)(desiredWidth * ((float)bmp.getHeight()/bmp.getWidth()));
        image = Bitmap.createScaledBitmap(bmp, desiredWidth, desiredHeight, true);

        x = startX;
        y = startY;
    }

    public void update() {
        x += (int) dx;
        y += (int) dy;
        // Garder dans l'écran
        x = Math.max(image.getWidth()/2, Math.min(x, this.screenWidth - image.getWidth()/2));
        y = Math.max(image.getHeight()/2, Math.min(y, this.screenHeight - image.getHeight()/2));
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x - (float) image.getWidth() /2, y - (float) image.getHeight() /2, null);
    }

    public void setMovement(float dx, float dy) {
        this.dx = dx * 10; // Ajuster la vitesse
        this.dy = dy * 10;
    }

    public Rect getCollisionRect() {
        return new Rect(
                x - image.getWidth()/2,
                y - image.getHeight()/2,
                x + image.getWidth()/2,
                y + image.getHeight()/2
        );
    }
}