package com.example.pet_track.models.response;

import java.util.List;

public class PagingResponse<T> {
    public List<T> Items ;
    public int pageNumber ;
    public int totalPages;
    public int totalCount ;
    public int pageSize ;

    public List<T> getItems() {
        return Items;
    }

    public void setItems(List<T> items) {
        Items = items;
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
