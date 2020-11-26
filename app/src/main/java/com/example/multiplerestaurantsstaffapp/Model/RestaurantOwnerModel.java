package com.example.multiplerestaurantsstaffapp.Model;

import java.util.List;

public class RestaurantOwnerModel {
    private boolean success;
    private String message;
    private List<RestaurantOwner> result;

    public RestaurantOwnerModel() {
    }

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

    public List<RestaurantOwner> getResult() {
        return result;
    }

    public void setResult(List<RestaurantOwner> result) {
        this.result = result;
    }
}
