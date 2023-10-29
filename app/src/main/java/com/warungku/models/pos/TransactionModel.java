package com.warungku.models.pos;

import java.util.List;

public class TransactionModel {
    private Integer id;
    private Integer user_id;
    private String name;
    private Long timestamp;
    private Integer status;
    private Double total;
    private List<TrxDetailModel> details;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer userId) {
        this.user_id = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<TrxDetailModel> getDetails() {
        return details;
    }

    public void setDetails(List<TrxDetailModel> details) {
        this.details = details;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
