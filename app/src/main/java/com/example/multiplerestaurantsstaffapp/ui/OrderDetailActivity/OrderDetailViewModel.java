package com.example.multiplerestaurantsstaffapp.ui.OrderDetailActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.multiplerestaurantsstaffapp.Common.Common;
import com.example.multiplerestaurantsstaffapp.Interface.DataLoadListner;
import com.example.multiplerestaurantsstaffapp.Model.OrderDetailModel;
import com.example.multiplerestaurantsstaffapp.Retrofit.IMyRestaurantAPI;
import com.example.multiplerestaurantsstaffapp.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OrderDetailViewModel extends AndroidViewModel {

    DataLoadListner mdataLoadListner;
    IMyRestaurantAPI myRestaurantAPI;
    MutableLiveData<OrderDetailModel> orderDetailModelMutableLiveData;
    CompositeDisposable disposable = new CompositeDisposable();

    public OrderDetailViewModel(@NonNull Application application) {
        super(application);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        orderDetailModelMutableLiveData = new MutableLiveData();
    }

    public void onDestroy() {
        disposable.clear();
    }

    public MutableLiveData<OrderDetailModel> loadOrderDetails(DataLoadListner dataLoadListner) {
        this.mdataLoadListner =dataLoadListner;
        if (orderDetailModelMutableLiveData != null) {
            disposable.add(myRestaurantAPI.getOrderDetail(Common.API_KEY,
                    Common.currentOrder.getOrderId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(orderDetailModel -> {
                        mdataLoadListner.onLoadSuccess();
                        orderDetailModelMutableLiveData.setValue(orderDetailModel);
                    }, throwable -> {
                        mdataLoadListner.onLoadFailed(throwable.getMessage());
                    }));
        }
        return orderDetailModelMutableLiveData;
    }
}
