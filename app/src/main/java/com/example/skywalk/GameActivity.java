package com.example.skywalk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private ImageView joystickCenter;
    private View joystickExterior;
    private float maxDistance;

    // Pour le contrôle par inclinaison
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorListener;
    private boolean isJoystickActive = false; // Le joystick a la priorité

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Optionnel : forcer l'orientation portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        joystickCenter = findViewById(R.id.joystick_center);
        joystickExterior = findViewById(R.id.joystick_exterior);

        // Calcul initial de maxDistance (basé sur la largeur de joystick_exterior)
        joystickExterior.post(() -> {
            maxDistance = joystickExterior.getWidth() / 2f - joystickCenter.getWidth() / 2f;
        });

        setupControls();
        setupRetryButton();
        setupSensor();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupControls() {
        joystickExterior.setOnTouchListener((v, event) -> {
            // Position absolue de joystick_exterior dans le layout parent
            float exteriorPosX = joystickExterior.getX();
            float exteriorPosY = joystickExterior.getY();
            // Centre absolu de joystick_exterior
            float exteriorCenterX = exteriorPosX + joystickExterior.getWidth() / 2f;
            float exteriorCenterY = exteriorPosY + joystickExterior.getHeight() / 2f;

            float touchX = exteriorPosX + event.getX();
            float touchY = exteriorPosY + event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    isJoystickActive = true;
                    // Calcul de la distance entre le toucher et le centre
                    float distance = (float) Math.sqrt(Math.pow(touchX - exteriorCenterX, 2)
                            + Math.pow(touchY - exteriorCenterY, 2));
                    if (distance > maxDistance) {
                        touchX = exteriorCenterX + (touchX - exteriorCenterX) * maxDistance / distance;
                        touchY = exteriorCenterY + (touchY - exteriorCenterY) * maxDistance / distance;
                    }
                    // Positionner le joystick_center pour qu'il suive le doigt
                    joystickCenter.setX(touchX - joystickCenter.getWidth() / 2f);
                    joystickCenter.setY(touchY - joystickCenter.getHeight() / 2f);
                    // Calculer le vecteur de déplacement relatif au centre du joystick_exterior
                    float dx = (touchX - exteriorCenterX) / maxDistance;
                    float dy = (touchY - exteriorCenterY) / maxDistance;
                    gameView.setPlayerMovement(dx, dy);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isJoystickActive = false;
                    // Optionnel : réinitialiser la position du joystick_center au centre de joystick_exterior
                    joystickCenter.setX(exteriorCenterX - joystickCenter.getWidth() / 2f);
                    joystickCenter.setY(exteriorCenterY - joystickCenter.getHeight() / 2f);
                    // Arrêter le mouvement (ou laisser le contrôle par inclinaison reprendre)
                    gameView.setPlayerMovement(0, 0);
                    break;
            }
            return true;
        });
    }

    private void setupSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // Si le joystick n'est pas utilisé, on contrôle le déplacement par l'inclinaison
                if (!isJoystickActive) {
                    // Pour une utilisation simple :
                    //   - L'axe X du capteur contrôle le déplacement horizontal.
                    //   - L'axe Y (après calibration) contrôle le déplacement vertical.
                    // En orientation portrait, à l'état de repos, event.values[0] ~ 0 et event.values[1] ~ 9.81.
                    // On soustrait 9.81 sur l'axe Y pour obtenir 0 en repos.
                    float dx = -event.values[0] / 9.81f; // inversion pour avoir le bon sens
                    float dy = (event.values[1] - 9.81f) / 9.81f;

                    // On peut limiter les valeurs à [-1, 1] si nécessaire
                    dx = Math.max(-1, Math.min(dx, 1));
                    dy = Math.max(-1, Math.min(dy, 1));

                    gameView.setPlayerMovement(dx, dy);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Pas utilisé ici
            }
        };
    }

    private void setupRetryButton() {
        findViewById(R.id.retryButton).setOnClickListener(v -> recreate());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            gameView.resume();
        }
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pause();
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorListener);
        }
    }

    public void showGameOver() {
        runOnUiThread(() -> findViewById(R.id.gameOverLayout).setVisibility(View.VISIBLE));
    }
}
