package com.example.pet_track;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.pet_track.ui.login.LoginActivity;
import com.example.pet_track.ui.wallet.WalletActivity;
import com.example.pet_track.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pet_track.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_booking_history )
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // MVVM: Use UserViewModel to observe user info
        View headerView = navigationView.getHeaderView(0);
        ImageView avatar = headerView.findViewById(R.id.imageViewAvatar);
        TextView name = headerView.findViewById(R.id.textViewName);
        TextView email = headerView.findViewById(R.id.textViewEmail);
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getFullName().observe(this, name::setText);
        userViewModel.getEmail().observe(this, email::setText);
        userViewModel.getAvatarUrl().observe(this, url -> {
            if (url != null && !url.isEmpty()) {
                Glide.with(this).load(url).placeholder(R.mipmap.ic_launcher_round).into(avatar);
            } else {
                avatar.setImageResource(R.mipmap.ic_launcher_round);
            }
        });

        // Handle logout
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                userViewModel.clearUserInfo();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_wallet) {
                Intent intent = new Intent(this, WalletActivity.class);
                startActivity(intent);
                return true;
            }
            // Default navigation handling
            return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}