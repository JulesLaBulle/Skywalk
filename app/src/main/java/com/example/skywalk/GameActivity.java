package com.example.skywalk;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private ImageView joystickCenter;
    private float centerX, centerY;
    private float maxDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        joystickCenter = findViewById(R.id.joystick_center);

        // Position initiale du joystick
        joystickCenter.post(() -> {
            centerX = joystickCenter.getX() + (float) joystickCenter.getWidth() /2;
            centerY = joystickCenter.getY() + (float) joystickCenter.getHeight() /2;
            maxDistance = (float) findViewById(R.id.joystick_exterior).getWidth() /2 - (float) joystickCenter.getWidth() /2;
        });

        setupControls();
        setupRetryButton();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupControls() {
        findViewById(R.id.joystick_exterior).setOnTouchListener((v, event) -> {
            float touchX = event.getX();
            float touchY = event.getY();

            // Calcul de la distance depuis le centre
            float distance = (float)Math.sqrt(Math.pow(touchX - centerX, 2) + Math.pow(touchY - centerY, 2));

            if(distance > maxDistance) {
                touchX = centerX + (touchX - centerX) * maxDistance / distance;
                touchY = centerY + (touchY - centerY) * maxDistance / distance;
            }

            // Déplacement du centre du joystick
            joystickCenter.setX(touchX - (float) joystickCenter.getWidth() /2);
            joystickCenter.setY(touchY - (float) joystickCenter.getHeight() /2);

            // Calcul du vecteur de direction
            float dx = (touchX - centerX) / maxDistance;
            float dy = (touchY - centerY) / maxDistance;
            gameView.setPlayerMovement(dx, dy);

            return true;
        });
    }

    public void showGameOver() {
        runOnUiThread(() -> findViewById(R.id.gameOverLayout).setVisibility(View.VISIBLE));
    }

    private void setupRetryButton() {
        findViewById(R.id.retryButton).setOnClickListener(v -> recreate());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(gameView != null) {
            gameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(gameView != null) {
            gameView.resume();
        }
    }
}