package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pet_track.R;
import com.example.pet_track.models.response.ClinicResponse;
import com.example.pet_track.ui.profile.ProfileActivity;
import com.example.pet_track.utils.HeaderHelper;
import com.example.pet_track.utils.SharedPreferencesManager;

import java.util.List;

public class ClinicListFragment extends Fragment {

    private ClinicListViewModel clinicListViewModel;
    private LinearLayout clinicsContainer;
    private TextView cartBadge;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clinic_list, container, false);

        clinicsContainer = rootView.findViewById(R.id.clinics_container);

        // Lấy TextView từ header_booking.xml đã include sẵn
        TextView tvHello = rootView.findViewById(R.id.tv_hello);
        String fullName = SharedPreferencesManager.getInstance(requireContext()).getFullName();
        tvHello.setText("Xin chào, " + fullName);

        clinicListViewModel = new ViewModelProvider(this).get(ClinicListViewModel.class);

        clinicListViewModel.getClinics().observe(getViewLifecycleOwner(), clinics -> {
            if (clinics != null) {
                displayClinics(clinics);
            }
        });

        clinicListViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        cartBadge = rootView.findViewById(R.id.cart_badge);
        HeaderHelper.setupCartHeader(rootView, requireContext());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cartBadge != null) {
            HeaderHelper.updateCartBadge(cartBadge, requireContext()); // ✅ cập nhật lại khi fragment resume
        }
    }

    private void displayClinics(List<ClinicResponse> clinics) {
        if (getContext() == null) return;

        LinearLayout listHolder = clinicsContainer.findViewById(R.id.clinic_list_holder);
        listHolder.removeAllViews(); // ✅ chỉ xoá phần list

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (ClinicResponse clinic : clinics) {
            View clinicView = inflater.inflate(R.layout.clinic_card, listHolder, false);

            TextView clinicName = clinicView.findViewById(R.id.clinic_name);
            TextView clinicAddress = clinicView.findViewById(R.id.clinic_address);
            TextView clinicPhone = clinicView.findViewById(R.id.clinic_phone);
            TextView clinicTime = clinicView.findViewById(R.id.clinic_time);
            ImageView clinicBanner = clinicView.findViewById(R.id.clinic_icon);
            Button btnBooking = clinicView.findViewById(R.id.btn_booking);
            Button btnViewMap = clinicView.findViewById(R.id.btn_view_map);
            clinicName.setText(clinic.getName());
            clinicAddress.setText(clinic.getAddress());
            clinicPhone.setText(clinic.getPhoneNumber());
            btnViewMap.setOnClickListener(v -> {
                String address = clinic.getAddress();
                if (!address.isEmpty()) {
                    try {
                        String encodedAddress = Uri.encode(address);
                        String mapUri = "geo:0,0?q=" + encodedAddress;

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));

            // Set ảnh banner nếu có, nếu không thì để ảnh mặc định
            Log.d("BannerDebug", "URL: " + clinic.getBannerUrl());

            if (clinic.getBannerUrl() != null && !clinic.getBannerUrl().isEmpty()) {
                Glide.with(requireContext())
                        .load(clinic.getBannerUrl())
                        .placeholder(R.drawable.ic_clinic)
                        .error(R.drawable.ic_clinic)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(clinicBanner);
            } else {
                clinicBanner.setImageResource(R.drawable.ic_clinic); // fallback nếu null
            }

                        // Sử dụng Intent Chooser để hiển thị tất cả ứng dụng có thể xử lý
                        Intent chooser = Intent.createChooser(mapIntent, "Chọn ứng dụng bản đồ");

                        if (chooser.resolveActivity(requireContext().getPackageManager()) != null) {
                            startActivity(chooser);
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy ứng dụng bản đồ", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi khi mở bản đồ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Địa chỉ không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            });
            // Lấy giờ làm việc từ schedule đầu tiên (nếu có)
            String time = "N/A";
            if (clinic.getSchedules() != null && !clinic.getSchedules().isEmpty()) {
                ClinicResponse.Schedule firstSchedule = clinic.getSchedules().get(0);
                time = firstSchedule.getOpenTime() + " - " + firstSchedule.getCloseTime();
            }
            clinicTime.setText(time);

            btnBooking.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ClinicDetailsActivity.class);
                intent.putExtra("clinic_id", clinic.getId());
                startActivity(intent);
            });

            clinicsContainer.addView(clinicView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clinicsContainer = null;
    }
} 