package com.example.multiplerestaurantsstaffapp.EventBus;

public class OrdersRefreshEvent {
    private  Boolean isRefreshed;

    public OrdersRefreshEvent(Boolean isRefreshed) {
        this.isRefreshed = isRefreshed;
    }

    public Boolean getRefreshed() {
        return isRefreshed;
    }

    public void setRefreshed(Boolean refreshed) {
        isRefreshed = refreshed;
    }
}
