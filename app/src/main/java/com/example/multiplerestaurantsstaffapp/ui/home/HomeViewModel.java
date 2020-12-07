package com.example.multiplerestaurantsstaffapp.ui.home;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.multiplerestaurantsstaffapp.Common.Common;
import com.example.multiplerestaurantsstaffapp.Interface.IMaxOrderListner;
import com.example.multiplerestaurantsstaffapp.Model.MaxOrder;
import com.example.multiplerestaurantsstaffapp.Model.MaxOrderModel;
import com.example.multiplerestaurantsstaffapp.Model.OrderModel;
import com.example.multiplerestaurantsstaffapp.Retrofit.IMyRestaurantAPI;
import com.example.multiplerestaurantsstaffapp.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<MaxOrderModel> maxOrderModelMutableLiveData;
    IMaxOrderListner maxOrderListner;
    MutableLiveData<OrderModel> orderModelMutableLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        maxOrderModelMutableLiveData = new MutableLiveData();
        orderModelMutableLiveData = new MutableLiveData();
//        maxOrderListner = new IMaxOrderListner();
    }

    public void onDestroy() {
        compositeDisposable.clear();
    }


    public MutableLiveData<MaxOrderModel> getMaxOrder() {

        if (maxOrderModelMutableLiveData != null) {
            compositeDisposable.add(
                    myRestaurantAPI.getMaxOrder(Common.API_KEY,
                            String.valueOf(Common.currentRestaurantOwner.getRestaurantId()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(maxOrderModel -> {
                                maxOrderModelMutableLiveData.setValue(maxOrderModel);
                                // maxOrderListner.onMaxOrderSuccess("success");
                            }, throwable -> {
                                Toast.makeText(getApplication(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );
        }
        return maxOrderModelMutableLiveData;
    }

    public MutableLiveData<OrderModel> getAllOrder(int from, int to) {

        if (orderModelMutableLiveData != null) {
            compositeDisposable.add(
                    myRestaurantAPI.getOrder(Common.API_KEY,
                            String.valueOf(Common.currentRestaurantOwner.getRestaurantId()),
                            from, to)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(orderModel -> {
                                orderModelMutableLiveData.setValue(orderModel);
                            }, throwable -> {
                                Toast.makeText(getApplication(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                            })

            );

        }
        return orderModelMutableLiveData;
    }
}