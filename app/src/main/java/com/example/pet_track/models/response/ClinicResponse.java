package com.example.pet_track.models.response;

import com.example.pet_track.models.response.ServicePackage;

import java.util.List;

public class ClinicResponse {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String slogan;
    private String description;
    private String bannerUrl;
    private String status;
    private String createdTime;
    private String ownerUserId;
    private String ownerFullName;
    private List<Schedule> schedules;
    private List<ServicePackage> servicePackages;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public List<Schedule> getSchedules() { return schedules; }
    public List<ServicePackage> getServicePackages() { return servicePackages; }
    public String getSlogan() { return slogan; }
    public String getDescription() { return description; }

    public static class Schedule {
        private int dayOfWeek;
        private String openTime;
        private String closeTime;
        public int getDayOfWeek() { return dayOfWeek; }
        public String getOpenTime() { return openTime; }
        public String getCloseTime() { return closeTime; }
    }
} 