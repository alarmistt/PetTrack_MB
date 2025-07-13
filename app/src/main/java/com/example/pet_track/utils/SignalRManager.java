package com.example.pet_track.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.pet_track.models.response.MessageResponse;
import com.google.gson.Gson;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Single;

public class SignalRManager {
    private static final String TAG = "SignalRManager";
    private static final String HUB_URL = "http://10.0.2.2:5195/chathub";
    private static SignalRManager instance;

    private HubConnection hubConnection;
    private MessageListener messageListener;
    private boolean isManuallyStopped = false;

    public interface MessageListener {
        void onNewMessage(MessageResponse message);
    }

    public static synchronized SignalRManager getInstance() {
        if (instance == null) {
            instance = new SignalRManager();
        }
        return instance;
    }

    public void setListener(@Nullable MessageListener listener) {
        this.messageListener = listener;
    }

    public void startConnection(@Nullable String token) {
        if (token == null || token.trim().isEmpty()) {
            Log.e(TAG, "Token null hoặc rỗng. Không thể khởi động SignalR.");
            return;
        }

        if (hubConnection != null &&
                (hubConnection.getConnectionState() == HubConnectionState.CONNECTED ||
                        hubConnection.getConnectionState() == HubConnectionState.CONNECTING)) {
            return;
        }

        isManuallyStopped = false;

        hubConnection = HubConnectionBuilder.create(HUB_URL)
                .withAccessTokenProvider(Single.fromCallable(() -> {
                    if (token == null || token.trim().isEmpty()) {
                        throw new IllegalStateException("Token không hợp lệ khi kết nối SignalR.");
                    }
                    return token;
                }))
                .withHeader("Accept", "application/json")
                .build();

        registerHandlers();

        hubConnection.start()
                .doOnComplete(() -> Log.i(TAG, "✅ SignalR đã kết nối"))
                .doOnError(error -> {
                    Log.e(TAG, "❌ Lỗi kết nối SignalR: " + error.getMessage(), error);
                    scheduleReconnect(token);
                })
                .subscribe();
    }

    private void registerHandlers() {
        if (hubConnection != null) {
            hubConnection.on("ReceiveMessage", (msg) -> {
                Log.d(TAG, "📩 Received SignalR message: " + msg.getContent());
                if (messageListener != null) {
                    messageListener.onNewMessage(msg);
                }
            }, MessageResponse.class);
        }
    }

    public void stopConnection() {
        isManuallyStopped = true;

        if (hubConnection != null) {
            if (hubConnection.getConnectionState() != HubConnectionState.DISCONNECTED) {
                hubConnection.stop();
                Log.i(TAG, "🔌 SignalR đã dừng kết nối.");
            }
            hubConnection = null;
        }
    }

    private void scheduleReconnect(String token) {
        if (isManuallyStopped) return;

        new Thread(() -> {
            try {
                Log.i(TAG, "⏳ Thử kết nối lại sau 5s...");
                TimeUnit.SECONDS.sleep(5);
                startConnection(token);
            } catch (InterruptedException e) {
                Log.e(TAG, "Reconnect bị gián đoạn: " + e.getMessage());
            }
        }).start();
    }

    public HubConnectionState getConnectionState() {
        return hubConnection != null ? hubConnection.getConnectionState() : HubConnectionState.DISCONNECTED;
    }
}
