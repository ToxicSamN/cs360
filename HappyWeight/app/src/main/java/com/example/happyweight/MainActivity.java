package com.example.happyweight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private NavController navController;

    private Toolbar toolbar;
    private BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.loginFragment,
                R.id.weightOverviewFragment,
                R.id.logoutFragment
        ).build();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        this.setSupportActionBar(toolbar);

        bottom_nav = findViewById(R.id.bottom_nav);
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.weightOverviewFragment) {
                    item.setChecked(true);
                    navController.popBackStack();
                    navController.navigate(R.id.weightOverviewFragment);
                } else if (item.getItemId() == R.id.weightTrackerFragment){
                    item.setChecked(true);
                    navController.popBackStack();
                    navController.navigate(R.id.weightTrackerFragment);
                }
                return false;
            }
        });

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        FirebaseUser user = fAuth.getCurrentUser();

        // menu item 0 = Settings
        // menu item 1 = Logout
        if (user == null){
            // disable Logout option
            menu.getItem(1).setEnabled(false);
        } else {
            // Enable Logout option
            menu.getItem(1).setEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.optSettings){
            navController.navigate(SettingsFragmentDirections.actionGlobalSettingsFragment());
        } else if (item.getItemId() == R.id.optLogout){
            navController.navigate(LogoutFragmentDirections.actionGlobalLogoutFragment());
        }
        return super.onOptionsItemSelected(item);
    }
}