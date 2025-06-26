package com.example.pet_track.ui.wallet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pet_track.R;
import com.example.pet_track.models.response.wallet.TopUpResponse;
import com.example.pet_track.utils.SharedPreferencesManager;
import com.example.pet_track.viewmodel.WalletViewModel;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {
    private WalletViewModel walletViewModel;
    private TextView tvBalance;
    private RecyclerView recyclerTopUpHistory;
    private TopUpHistoryAdapter adapter;
    private List<TopUpResponse> transactionList = new ArrayList<>();
    private static final String TAG = "WalletFragment";

    // Phân trang
    private int currentPage = 1;
    private final int pageSize = 5;
    private boolean isLoading = false;

    private String token, userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        tvBalance = view.findViewById(R.id.tvBalance);
        recyclerTopUpHistory = view.findViewById(R.id.recyclerTopUpHistory);

        // Setup RecyclerView
        adapter = new TopUpHistoryAdapter(transactionList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerTopUpHistory.setLayoutManager(layoutManager);
        recyclerTopUpHistory.setAdapter(adapter);

        // Scroll listener để load thêm trang
        recyclerTopUpHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                if (!rv.canScrollVertically(1) && !isLoading) {
                    isLoading = true;
                    currentPage++;
                    walletViewModel.fetchTopUpHistory(token, currentPage, pageSize, userId, null);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);

        token = SharedPreferencesManager.getInstance(requireContext()).getToken();
        userId = SharedPreferencesManager.getInstance(requireContext()).getUserId();
        Log.d(TAG, "Token: " + token);
        Log.d(TAG, "id: " + userId);

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Balance
        walletViewModel.getWallet().observe(getViewLifecycleOwner(), wallet -> {
            if (wallet != null) {
                tvBalance.setText("Số dư: " + wallet.getBalance() + " VNĐ");
            } else {
                Toast.makeText(requireContext(), "Không lấy được thông tin ví!", Toast.LENGTH_SHORT).show();
            }
        });

        // Transaction
        walletViewModel.getTopUpHistory().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                if (currentPage == 1) transactionList.clear();
                transactionList.addAll(list);
                adapter.notifyDataSetChanged();
            }
            isLoading = false;
        });

        // Error
        walletViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            isLoading = false;
        });

        // Fetch initial data
        walletViewModel.fetchWallet(token);
        walletViewModel.fetchTopUpHistory(token, currentPage, pageSize, userId, null);
    }
}
