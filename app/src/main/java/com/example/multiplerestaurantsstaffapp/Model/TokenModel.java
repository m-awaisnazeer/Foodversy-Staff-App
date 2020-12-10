package com.example.multiplerestaurantsstaffapp.Model;

import java.util.List;

public class TokenModel {
    private boolean success;
    private String message;
    private List<MyRestaurantToken> result;

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

    public List<MyRestaurantToken> getResult() {
        return result;
    }

    public void setResult(List<MyRestaurantToken> result) {
        this.result = result;
    }
}
