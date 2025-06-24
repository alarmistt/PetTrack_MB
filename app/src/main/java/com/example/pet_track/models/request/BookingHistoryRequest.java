package com.example.pet_track.models.request;

public class BookingHistoryRequest {
    private int pageIndex;
    private int pageSize;
    private String clinicId;
    private String userId;
    private String status;

    // Constructor
    public void BookingHistoryRequestRequest(int pageIndex, int pageSize, String clinicId, String userId, String status) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.clinicId = clinicId;
        this.userId = userId;
        this.status = status;
    }

    // Getter & Setter
    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
