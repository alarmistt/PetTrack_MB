package com.example.pet_track.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ServicePackage implements Parcelable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    // ---- Constructor mặc định ----
    public ServicePackage() {
    }

    // ---- Getter và Setter ----
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // ---- Parcelable ----
    protected ServicePackage(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
    }

    public static final Creator<ServicePackage> CREATOR = new Creator<ServicePackage>() {
        @Override
        public ServicePackage createFromParcel(Parcel in) {
            return new ServicePackage(in);
        }

        @Override
        public ServicePackage[] newArray(int size) {
            return new ServicePackage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0; // Không có File Descriptor đặc biệt
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
    }
}
