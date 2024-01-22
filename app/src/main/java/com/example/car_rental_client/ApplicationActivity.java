package com.example.car_rental_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

// Fragment handler
public class ApplicationActivity extends AppCompatActivity {
    BottomNavigationView navigationView;

    // Other relevant data I guess? (that we got from the login screen)
    String username;
    String sessionID;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            sessionID = extras.getString("sessionID");
            userID = extras.getInt("userID");
        }

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.cars_page);

        // instantiate fragments and pass bundle data we got from previous intent
        CarListFragment carListFragment = new CarListFragment();
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        BookingFragment bookingFragment = new BookingFragment();

        userProfileFragment.setArguments(extras);
        carListFragment.setArguments(extras);
        bookingFragment.setArguments(extras);

        // default fragment?
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, carListFragment).commit();
        }

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bookings_page) {
                    navigationView.getMenu().findItem(R.id.bookings_page).setChecked(true);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_fragment, bookingFragment)
                            .commit();
                    return true;
                    // go to bookings page
                } else if (item.getItemId() == R.id.profile_page) {
                    navigationView.getMenu().findItem(R.id.profile_page).setChecked(true);
                    //navigationView.setSelectedItemId(R.id.profile_page); this causes a stack overflow error (endless recursion) since it calls the listener
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_fragment, userProfileFragment)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.cars_page) {
                    navigationView.getMenu().findItem(R.id.cars_page).setChecked(true);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_fragment, carListFragment)
                            .commit();
                }
                return false;
            }
        });
    }
}