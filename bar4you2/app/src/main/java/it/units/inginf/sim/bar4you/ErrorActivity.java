package it.units.inginf.sim.bar4you;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        new CountDownTimer(2000, 500) { // 5 secondi di pausa totali 1 un secondo di intervallo tra tick
            public void onTick(long millisUntilFinished) {
                //Metodo vuoto, serve solo per il corretto funzionamento
            }

            public void onFinish() {
                // Quando il timer è scaduto, avvia l'attività MainActivity
                Intent intent = new Intent(ErrorActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Chiude questa attività dopo l'avvio dell'altra
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}