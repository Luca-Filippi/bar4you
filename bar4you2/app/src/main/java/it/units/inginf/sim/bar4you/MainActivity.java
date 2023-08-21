package it.units.inginf.sim.bar4you;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import com.google.firebase.FirebaseApp;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this); // Inizializzazione di Firebase
        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        if (savedInstanceState == null) {
            Fragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                if(sharedViewModel.getTableNumberMutableLiveData().getValue() != null) {
                    if (sharedViewModel.getTableNumberMutableLiveData().getValue() > 0) {
                        selectedFragment = new PersonsFragment();
                    } else {
                        selectedFragment = new HomeFragment();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    sharedViewModel.resetSharedViewModel();
                    startActivity(new Intent(MainActivity.this, ErrorActivity.class));
                }
            } else if (itemId == R.id.navigation_order) {
                selectedFragment = new OrderFragment();
            } else if (itemId == R.id.navigation_bill) {
                selectedFragment = new BillFragment();
            } else if (itemId == R.id.navigation_send) {
                selectedFragment = new SendFragment();
            } else {
                System.exit(1);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
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
        FirebaseApp.getInstance().delete();
    }
}