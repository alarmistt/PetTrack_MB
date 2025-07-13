package com.example.pet_track.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet_track.api.ApiServiceBuilder;
import com.example.pet_track.api.ChatService;
import com.example.pet_track.models.request.SendMessageRequest;
import com.example.pet_track.models.response.MessageResponse;
import com.example.pet_track.models.response.PaginatedResponse;
import com.example.pet_track.models.response.WrapResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends ViewModel {

    private final MutableLiveData<List<MessageResponse>> messagesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<MessageResponse>> getMessages() {
        return messagesLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadMessages(Context context, String clinicId) {
        ChatService chatService = ApiServiceBuilder.buildService(ChatService.class, context);

        chatService.getChatHistory(clinicId, 1, 50).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<WrapResponse<PaginatedResponse<MessageResponse>>> call, Response<WrapResponse<PaginatedResponse<MessageResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    messagesLiveData.postValue(response.body().getData().getItems());
                } else {
                    errorMessage.postValue("Failed to load messages");
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<PaginatedResponse<MessageResponse>>> call, Throwable t) {
                errorMessage.postValue("Error: " + t.getMessage());
            }
        });
    }

    public void sendMessage(Context context, String clinicId, String content) {
        ChatService chatService = ApiServiceBuilder.buildService(ChatService.class, context);
        SendMessageRequest request = new SendMessageRequest(clinicId, content);
        chatService.sendMessage(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<WrapResponse<MessageResponse>> call, Response<WrapResponse<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addIncomingMessage(response.body().getData());
                } else {
                    errorMessage.postValue("Send failed");
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<MessageResponse>> call, Throwable t) {
                errorMessage.postValue("Send error: " + t.getMessage());
            }
        });
    }

    public void addIncomingMessage(MessageResponse message) {
        List<MessageResponse> current = messagesLiveData.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(message);
        messagesLiveData.postValue(current);
    }
}

