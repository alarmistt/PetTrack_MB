package com.example.pet_track.models.response;

import com.google.gson.annotations.SerializedName;

public class Slot {
    @SerializedName("id")
    private String id;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
} 