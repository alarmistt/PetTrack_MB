package com.example.pet_track.api;

import com.example.pet_track.models.request.CreateLinkBookingRequest;
import com.example.pet_track.models.request.BookingRequest;
import com.example.pet_track.models.request.CreateLinkBookingRequest;
import com.example.pet_track.models.request.LoginRequest;
import com.example.pet_track.models.request.RegisterRequest;
import com.example.pet_track.models.request.BookingRequest;
import com.example.pet_track.models.response.BookingHistoryResponse;
import com.example.pet_track.models.response.LoginResponse;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.RegisterResponse;
import com.example.pet_track.models.response.UserResponse;
import com.example.pet_track.models.response.UserUpdateRequest;
import com.example.pet_track.models.response.Slot;
import com.example.pet_track.models.response.Slot;
import com.example.pet_track.models.response.UserResponse;
import com.example.pet_track.models.response.UserUpdateRequest;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.models.request.CreatePaymentRequest;
import com.example.pet_track.models.response.payment.CreatePaymentResult;
import com.example.pet_track.models.response.payment.PaymentResponse;
import com.example.pet_track.models.response.wallet.TopUpResponse;
import com.example.pet_track.models.response.wallet.TopUpResponse;
import com.example.pet_track.models.response.wallet.WalletResponse;
import com.example.pet_track.models.response.ClinicResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/Authentication/login")
    Call<WrapResponse<LoginResponse>> login(@Body LoginRequest request);
    @POST("api/Authentication/register")
    Call<WrapResponse<RegisterResponse>> register(@Body RegisterRequest request);
    @GET("api/Wallet/me")
    Call<WrapResponse<WalletResponse>> myWallet();
    @GET("api/Payment/get-history-transaction")
    Call<WrapResponse<PagingResponse<TopUpResponse>>> getTopUpTransaction(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("userId") String userId,
            @Query("status") String status
    );
    @POST("/api/Payment/check-status-transaction")
    Call<WrapResponse<PagingResponse<String>>> checkStatusTransaction(@Query("orderCode") String orderCode);
    @GET("api/Booking")
    Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> getBookings(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize,
            @Query("clinicId") String clinicId,
            @Query("userId") String userId,
            @Query("status") String status
    );
    @POST("api/Payment/create-booking-payment")
    Call<WrapResponse<CreatePaymentResult>> createBookingPayment(@Body CreateLinkBookingRequest createPaymentRequest);
    @GET("api/public/clinics/approved")
    Call<WrapResponse<List<ClinicResponse>>> getApprovedClinics();
    @POST("api/Payment/create-payment-intent")
    Call<WrapResponse<PaymentResponse>> createPaymentIntent(@Body CreatePaymentRequest createPaymentRequest);

    @GET("api/Slot/check-slot")
    Call<WrapResponse<List<Slot>>> getAvailableSlots(
            @Query("clinicId") String clinicId,
            @Query("apointmentDate") String appointmentDate
    );

    @GET("api/public/clinics/{clinicId}")
    Call<WrapResponse<ClinicResponse>> getClinicDetails(@Path("clinicId") String clinicId);

    @POST("api/Booking")
    Call<WrapResponse<Object>> createBooking(@Body BookingRequest request);

    @GET("api/Authentication/profile")
    Call<WrapResponse<UserResponse>> getProfile();
    @PUT("api/Authentication/profile")
    Call<Void> updateProfile(@Body UserUpdateRequest request);
}
