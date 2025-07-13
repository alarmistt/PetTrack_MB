package com.example.pet_track.models.response;

public class BookingHistoryResponse {
    private String id;
    private String userId;
    private String fullName;
    private String clinicId;
    private String clinicName;
    private String servicePackageId;
    private String servicePackageName;
    private String appointmentDate;
    private String status;
    private int price;
    private String createdTime;
    public  String getId() { return id; }
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

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
}
