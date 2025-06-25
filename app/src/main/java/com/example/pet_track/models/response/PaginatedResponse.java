package com.example.pet_track.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaginatedResponse<T> {
    @SerializedName("items")
    private List<T> items;

    @SerializedName("totalItems")
    private int totalItems;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalPages")
    private int totalPages;

    public List<T> getItems() {
        return items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
