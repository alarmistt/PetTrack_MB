package com.example.pet_track;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.pet_track.databinding.ActivityMainBinding;
import com.example.pet_track.ui.login.LoginActivity;
import com.example.pet_track.ui.profile.ProfileActivity;
import com.example.pet_track.viewmodel.UserViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // ✅ Thêm tất cả fragment ở đây để ActionBar hoạt động chính xác
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_wallet,
                R.id.nav_booking_history,
                R.id.nav_test_booking_payment // 🔥 bạn vừa thêm cái này
        ).setOpenableLayout(drawer).build();

        // Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();

        // Gắn toolbar và drawer với NavController
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Load user info lên menu trái
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

        // ✅ Navigation Drawer item listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_logout) {
                userViewModel.clearUserInfo();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }else if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            // ✅ Mặc định điều hướng cho các fragment còn lại
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) drawer.closeDrawers();
            return handled;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.menu_chat_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_chat) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            Bundle args = new Bundle();
            args.putString("clinicId", "user8"); // đang hard code clinicId, cần lấy từ dữ liệu người dùng mới đúng
            navController.navigate(R.id.nav_chat, args);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
