package com.example.pet_track.models.request;

public class SendMessageRequest {
    private String receiverId;
    private String content;

    public SendMessageRequest(String receiverId, String content) {
        this.receiverId = receiverId;
        this.content = content;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
