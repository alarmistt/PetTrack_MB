package com.example.pet_track.models.request;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    @SerializedName("slotId")
    private String slotId;

    @SerializedName("servicePackageId")
    private String servicePackageId;

    @SerializedName("appointmentDate")
    private String appointmentDate;

    public BookingRequest(String slotId, String servicePackageId, String appointmentDate) {
        this.slotId = slotId;
        this.servicePackageId = servicePackageId;
        this.appointmentDate = appointmentDate;
    }

    // Getters and setters if needed
} 