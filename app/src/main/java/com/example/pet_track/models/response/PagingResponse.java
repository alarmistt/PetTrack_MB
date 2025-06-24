package com.example.pet_track.models.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PagingResponse<T> {
    @SerializedName("items")
    private List<T> items;

    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("pageSize")
    private int pageSize;

    // Getters and Setters
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
