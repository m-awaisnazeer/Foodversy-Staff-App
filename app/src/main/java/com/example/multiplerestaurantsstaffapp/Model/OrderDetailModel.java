package com.example.multiplerestaurantsstaffapp.Model;

import java.util.List;

public class OrderDetailModel {

    private boolean success;
    private String message;
    private List<OrderDetail> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OrderDetail> getResult() {
        return result;
    }

    public void setResult(List<OrderDetail> result) {
        this.result = result;
    }
}
