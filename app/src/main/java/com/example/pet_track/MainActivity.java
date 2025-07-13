package com.example.pet_track;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.api.ApiServiceBuilder;
import com.example.pet_track.databinding.ActivityMainBinding;
import com.example.pet_track.models.response.BookingHistoryResponse;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.ui.login.LoginActivity;
import com.example.pet_track.ui.profile.ProfileActivity;
import com.example.pet_track.utils.SharedPreferencesManager;
import com.example.pet_track.viewmodel.UserViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "booking_channel_id";
    private static final int NOTIFICATION_ID = 1001;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_wallet,
                R.id.nav_booking_history,
                R.id.nav_test_booking_payment
        ).setOpenableLayout(drawer).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setupUserInfo(navigationView);
        setupNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            } else {
                checkPendingBookingsAndNotify();
            }
        } else {
            checkPendingBookingsAndNotify();
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_logout) {
                new ViewModelProvider(this).get(UserViewModel.class).clearUserInfo();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) drawer.closeDrawers();
            return handled;
        });
    }

    private void setupUserInfo(NavigationView navigationView) {
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
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Thông báo Booking",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo khi có booking chưa thanh toán");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void checkPendingBookingsAndNotify() {
        String userId = SharedPreferencesManager.getInstance(this).getUserId();
        ApiService apiService = ApiServiceBuilder.buildService(ApiService.class, this);

        apiService.getBookings(1, 20, "", userId, "Pending")
                .enqueue(new Callback<WrapResponse<PagingResponse<BookingHistoryResponse>>>() {
                    @Override
                    public void onResponse(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call,
                                           Response<WrapResponse<PagingResponse<BookingHistoryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PagingResponse<BookingHistoryResponse> pagingData = response.body().getData();
                            List<BookingHistoryResponse> items = (pagingData != null) ? pagingData.getItems() : null;

                            if (items != null && !items.isEmpty()) {
                                showBookingNotification();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call, Throwable t) {
                        Log.e("BookingDebug", "API lỗi: " + t.getMessage());
                    }
                });
    }

    private void showBookingNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Giỏ hàng chưa thanh toán")
                .setContentText("🛒 Bạn còn lịch hẹn đang chờ xử lý!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.w("Notification", "Permission POST_NOTIFICATIONS chưa được cấp");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPendingBookingsAndNotify();
            } else {
                Log.w("Notification", "Người dùng từ chối quyền POST_NOTIFICATIONS");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.menu_chat_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_chat) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            Bundle args = new Bundle();
            args.putString("clinicId", "user8");
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
