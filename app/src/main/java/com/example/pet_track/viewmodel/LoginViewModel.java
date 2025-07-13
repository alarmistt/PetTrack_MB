package com.example.pet_track.viewmodel;

                    import android.content.Context;
                    import android.util.Log;
                    import androidx.lifecycle.LiveData;
                    import androidx.lifecycle.MutableLiveData;
                    import androidx.lifecycle.ViewModel;

                    import com.example.pet_track.api.ApiClient;
                    import com.example.pet_track.api.ApiService;
                    import com.example.pet_track.api.ApiServiceBuilder;
                    import com.example.pet_track.models.request.LoginRequest;
                    import com.example.pet_track.models.response.LoginResponse;
                    import com.example.pet_track.models.response.WrapResponse;
                    import retrofit2.Call;
                    import retrofit2.Callback;
                    import retrofit2.Response;
                    import java.io.IOException;


public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

                        public LiveData<LoginResponse> getLoginResult() {
                            return loginResult;
                        }

                        public LiveData<String> getErrorMessage() {
                            return errorMessage;
                        }

                        public void login(Context context, String email, String password) {
                            ApiService authApi = ApiClient.getAnonymousClient().create(ApiService.class);
                            LoginRequest request = new LoginRequest(email, password);

                            authApi.login(request).enqueue(new Callback<WrapResponse<LoginResponse>>() {
                                @Override
                                public void onResponse(Call<WrapResponse<LoginResponse>> call, Response<WrapResponse<LoginResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                        loginResult.postValue(response.body().getData());
                                    } else {
                                        String errorBody = "";
                                        try {
                                            if (response.errorBody() != null) {
                                                errorBody = response.errorBody().string();
                                            }
                                        } catch (IOException e) {
                                            errorBody = "Error reading errorBody: " + e.getMessage();
                                        }
                                        String logMsg = "Login failed: " + response.message() + " | code: " + response.code() + " | errorBody: " + errorBody;
                                        Log.e("LoginViewModel", logMsg);
                                        errorMessage.postValue(logMsg);
                                    }
                                }

                                @Override
                                public void onFailure(Call<WrapResponse<LoginResponse>> call, Throwable t) {
                                    Log.e("LoginViewModel", "Login error", t);
                                    errorMessage.postValue("Login error: " + t.getMessage());
                                }
                            });
                        }
                    }