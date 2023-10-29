package com.warungku.utils.api.response;

import com.warungku.models.pos.TransactionModel;

import java.util.List;

public class ResponseTransactions {
    public int current_page;
    public int last_page;
    public List<TransactionModel> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public List<TransactionModel> getData() {
        return data;
    }

    public void setData(List<TransactionModel> data) {
        this.data = data;
    }
}
