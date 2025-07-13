package com.example.pet_track.ui.booking;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.BookingHistoryResponse;
import com.example.pet_track.models.response.ClinicResponse;
import com.example.pet_track.models.response.Slot;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.utils.SharedPreferencesManager;
import com.example.pet_track.models.request.BookingRequest;
import com.example.pet_track.models.response.BookingResponse;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.WrapResponse;

import java.util.ArrayList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingViewModel extends ViewModel {

    private final MutableLiveData<List<Slot>> slots = new MutableLiveData<>();
    private final MutableLiveData<ClinicResponse> clinicDetails = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<BookingResponse> bookingCreated = new MutableLiveData<>();
    private final MutableLiveData<List<BookingResponse>> userBookings = new MutableLiveData<>();

    public LiveData<BookingResponse> getBookingCreated() {
        return bookingCreated;
    }

    public LiveData<List<BookingResponse>> getUserBookings() {
        return userBookings;
    }
    public LiveData<List<Slot>> getSlots() {
        return slots;
    }

    public LiveData<ClinicResponse> getClinicDetails() {
        return clinicDetails;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void createBooking(Context context, String slotId, String servicePackageId, String appointmentDate,
                              Runnable onSuccess, java.util.function.Consumer<String> onError) {
        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(context);
        String token = spm.getToken();

        if (token == null) {
            onError.accept("Bạn chưa đăng nhập!");
            return;
        }

        BookingRequest request = new BookingRequest(slotId, servicePackageId, appointmentDate);
        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.createBooking(request).enqueue(new Callback<WrapResponse<BookingResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<BookingResponse>> call, Response<WrapResponse<BookingResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccess.run();
                } else {
                    onError.accept("Lỗi tạo booking. Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<BookingResponse>> call, Throwable t) {
                onError.accept("Lỗi kết nối: " + t.getMessage());
            }
        });
    }


    public void getUserBookings(Context context, String status) {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        String userId = sharedPreferencesManager.getUserId();
        String token = sharedPreferencesManager.getToken();

        if (token == null || userId == null) {
            errorMessage.postValue("Yêu cầu đăng nhập để xem giỏ hàng.");
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.getBookings(1, 50, "", userId, status).enqueue(new Callback<WrapResponse<PagingResponse<BookingHistoryResponse>>>() {
            @Override
            public void onResponse(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call,
                                   Response<WrapResponse<PagingResponse<BookingHistoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PagingResponse<BookingHistoryResponse> pagingData = response.body().getData();
                    List<BookingHistoryResponse> bookings = (pagingData != null) ? pagingData.getItems() : new ArrayList<>();
                    // Nếu cần convert sang BookingResponse thì mapping tại đây (nếu 2 class giống nhau thì có thể ép kiểu)
                    // hoặc bạn có thể sửa View để dùng BookingHistoryResponse luôn.
                } else {
                    errorMessage.postValue("Không thể tải giỏ hàng: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call, Throwable t) {
                errorMessage.postValue("Lỗi khi tải giỏ hàng: " + t.getMessage());
            }
        });
    }

    public void getAvailableSlots(Context context, String clinicId, String date) {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        String token = sharedPreferencesManager.getToken();

        if (token == null) {
            errorMessage.postValue("Yêu cầu đăng nhập để xem khung giờ.");
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.getAvailableSlots(clinicId, date).enqueue(new Callback<WrapResponse<List<Slot>>>() {
            @Override
            public void onResponse(Call<WrapResponse<List<Slot>>> call, Response<WrapResponse<List<Slot>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Slot> allSlots = response.body().getData();
                    if (allSlots != null) {
                        slots.postValue(allSlots);
                    } else {
                        slots.postValue(null);
                    }
                } else {
                    errorMessage.postValue("Failed to get slots: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<List<Slot>>> call, Throwable t) {
                errorMessage.postValue("Error getting slots: " + t.getMessage());
            }
        });
    }

    public void getClinicDetails(String clinicId) {
        ApiService apiService = ApiClient.getAnonymousClient().create(ApiService.class);
        apiService.getClinicDetails(clinicId).enqueue(new Callback<WrapResponse<ClinicResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<ClinicResponse>> call, Response<WrapResponse<ClinicResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clinicDetails.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to get clinic details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<ClinicResponse>> call, Throwable t) {
                errorMessage.postValue("Error getting clinic details: " + t.getMessage());
            }
        });
    }
} 