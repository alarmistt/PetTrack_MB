package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet_track.R;
import com.example.pet_track.models.response.ClinicResponse;
import com.example.pet_track.utils.SharedPreferencesManager;

import java.util.List;

public class ClinicListFragment extends Fragment {

    private ClinicListViewModel clinicListViewModel;
    private LinearLayout clinicsContainer;

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

        return rootView;
    }

    private void displayClinics(List<ClinicResponse> clinics) {
        if (getContext() == null) return;

        LinearLayout listHolder = clinicsContainer.findViewById(R.id.clinic_list_holder);
        listHolder.removeAllViews(); // ✅ chỉ xoá phần list

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (ClinicResponse clinic : clinics) {
            View clinicView = inflater.inflate(R.layout.clinic_card, clinicsContainer, false);

            TextView clinicName = clinicView.findViewById(R.id.clinic_name);
            TextView clinicAddress = clinicView.findViewById(R.id.clinic_address);
            TextView clinicPhone = clinicView.findViewById(R.id.clinic_phone);
            TextView clinicTime = clinicView.findViewById(R.id.clinic_time);
            Button btnBooking = clinicView.findViewById(R.id.btn_booking);

            clinicName.setText(clinic.getName());
            clinicAddress.setText(clinic.getAddress());
            clinicPhone.setText(clinic.getPhoneNumber());

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