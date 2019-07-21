package com.rakesh.triptracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rakesh.triptracker.fragment.AddFragment;
import com.rakesh.triptracker.fragment.HomeFragment;
import com.rakesh.triptracker.fragment.MapFragment;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    FrameLayout container;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
             = new BottomNavigationView.OnNavigationItemSelectedListener() {

        Fragment fragment = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(new HomeFragment());
                    return true;
                case R.id.navigation_explore:
                    fragment = new MapFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_add:
                    loadFragment(new AddFragment());
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment) {
        //load Fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        container = findViewById(R.id.main_container);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }
}
