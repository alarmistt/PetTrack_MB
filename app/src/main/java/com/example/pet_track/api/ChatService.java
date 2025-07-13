package com.example.pet_track.api;

import com.example.pet_track.models.request.SendMessageRequest;
import com.example.pet_track.models.response.MessageResponse;
import com.example.pet_track.models.response.PaginatedResponse;
import com.example.pet_track.models.response.WrapResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatService {
    @POST("api/Chat/send")
    Call<WrapResponse<MessageResponse>> sendMessage(@Body SendMessageRequest request);

    @GET("api/Chat/history")
    Call<WrapResponse<PaginatedResponse<MessageResponse>>> getChatHistory(
            @Query("clinicId") String clinicId,
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize
    );

    @GET("api/Chat/new")
    Call<WrapResponse<List<MessageResponse>>> getNewMessages(
            @Query("clinicId") String clinicId,
            @Query("since") String isoDateTime // e.g. 2024-06-23T14:30:00Z
    );
}
