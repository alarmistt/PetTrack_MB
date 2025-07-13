package com.example.pet_track.models.response;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class BookingResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("clinicId")
    private String clinicId;

    @SerializedName("clinicName")
    private String clinicName;

    @SerializedName("servicePackageId")
    private String servicePackageId;

    @SerializedName("servicePackageName")
    private String servicePackageName;

    @SerializedName("appointmentDate")
    private String appointmentDate;

    @SerializedName("status")
    private String status;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("createdTime")
    private String createdTime;

    // Getters & Setters (bạn có thể dùng Lombok để tự sinh nếu muốn)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    public String getServicePackageId() { return servicePackageId; }
    public void setServicePackageId(String servicePackageId) { this.servicePackageId = servicePackageId; }

    public String getServicePackageName() { return servicePackageName; }
    public void setServicePackageName(String servicePackageName) { this.servicePackageName = servicePackageName; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
}

