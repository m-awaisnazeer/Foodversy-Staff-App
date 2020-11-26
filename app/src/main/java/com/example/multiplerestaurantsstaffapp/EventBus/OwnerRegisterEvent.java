package com.example.multiplerestaurantsstaffapp.EventBus;

public class OwnerRegisterEvent {
    private boolean ownerRegistration;

    public OwnerRegisterEvent(boolean ownerRegistration) {
        this.ownerRegistration = ownerRegistration;
    }

    public boolean isOwnerRegistration() {
        return ownerRegistration;
    }

    public void setOwnerRegistration(boolean ownerRegistration) {
        this.ownerRegistration = ownerRegistration;
    }
}
