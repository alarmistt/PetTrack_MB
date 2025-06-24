// app/src/main/java/com/example/pet_track/ui/wallet/WalletActivity.java
          package com.example.pet_track.ui.wallet;

          import android.content.Intent;
          import android.os.Bundle;
          import android.util.Log;
          import android.widget.TextView;
          import android.widget.Toast;

          import androidx.appcompat.app.AppCompatActivity;
          import androidx.lifecycle.ViewModelProvider;

          import com.example.pet_track.R;
          import com.example.pet_track.utils.SharedPreferencesManager;
          import com.example.pet_track.viewmodel.WalletViewModel;
          import com.example.pet_track.ui.login.LoginActivity;

          public class WalletActivity extends AppCompatActivity {
              private WalletViewModel walletViewModel;
              private TextView tvBalance;
              private static final String TAG = "WalletActivity";

              @Override
              protected void onCreate(Bundle savedInstanceState) {
                  super.onCreate(savedInstanceState);

                  String token = SharedPreferencesManager.getInstance(this).getToken();
                  Log.d(TAG, "Token: " + token);

                  if (token == null || token.isEmpty()) {
                      Log.e(TAG, "Token is missing, redirecting to LoginActivity");
                      Intent intent = new Intent(this, LoginActivity.class);
                      startActivity(intent);
                      finish();
                      return;
                  }

                  setContentView(R.layout.activity_wallet);

                  tvBalance = findViewById(R.id.tvBalance);

                  walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);

                  walletViewModel.getWallet().observe(this, wallet -> {
                      if (wallet != null) {
                          tvBalance.setText("Số dư: " + wallet.getBalance() + " VNĐ");
                      } else {
                          Log.e(TAG, "Wallet data is null");
                      }
                  });

                  walletViewModel.getError().observe(this, error -> {
                      Log.e(TAG, "Wallet error: " + error);
                      Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                  });

                  walletViewModel.fetchWallet(token);
              }
          }