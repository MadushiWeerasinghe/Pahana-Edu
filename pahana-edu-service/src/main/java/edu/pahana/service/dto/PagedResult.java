package edu.pahana.service.dto;

import java.util.List;

public class PagedResult<T> {
    public int total;
    public int page;
    public int size;
    public List<T> data;

    public PagedResult() {}
    public PagedResult(int total, int page, int size, List<T> data) {
        this.total = total; this.page = page; this.size = size; this.data = data;
    }
}
