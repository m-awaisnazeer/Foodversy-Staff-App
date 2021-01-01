package com.example.multiplerestaurantsstaffapp.ui.OrderDetailActivity;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.multiplerestaurantsstaffapp.Common.Common;
import com.example.multiplerestaurantsstaffapp.Interface.DataLoadListner;
import com.example.multiplerestaurantsstaffapp.Model.FCMResponse;
import com.example.multiplerestaurantsstaffapp.Model.FCMSendData;
import com.example.multiplerestaurantsstaffapp.Model.OrderDetailModel;
import com.example.multiplerestaurantsstaffapp.Model.TokenModel;
import com.example.multiplerestaurantsstaffapp.Model.UpdateOrderModel;
import com.example.multiplerestaurantsstaffapp.Retrofit.IFCMService;
import com.example.multiplerestaurantsstaffapp.Retrofit.IMyRestaurantAPI;
import com.example.multiplerestaurantsstaffapp.Retrofit.RetrofitClient;
import com.example.multiplerestaurantsstaffapp.Retrofit.RetrofitFCMClient;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrderDetailViewModel extends AndroidViewModel {

    DataLoadListner mdataLoadListner;
    IMyRestaurantAPI myRestaurantAPI;
    IFCMService ifcmService;

    MutableLiveData<OrderDetailModel> orderDetailModelMutableLiveData;
    MutableLiveData<UpdateOrderModel> updateOrderMutableLiveData;
    CompositeDisposable disposable = new CompositeDisposable();

    public OrderDetailViewModel(@NonNull Application application) {
        super(application);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        orderDetailModelMutableLiveData = new MutableLiveData();
        updateOrderMutableLiveData = new MutableLiveData();
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
    }

    public void onDestroy() {
        disposable.clear();
    }

    public MutableLiveData<OrderDetailModel> loadOrderDetails(DataLoadListner dataLoadListner) {
        this.mdataLoadListner = dataLoadListner;
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

    public MutableLiveData<UpdateOrderModel> updateOrderStatue(int convertStringToStatus) {
        if (updateOrderMutableLiveData != null) {
            disposable.add(myRestaurantAPI.updateOrderStatus(Common.API_KEY,
                    Common.currentOrder.getOrderId(),
                    convertStringToStatus)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(updateOrderModel -> {
                        updateOrderMutableLiveData.setValue(updateOrderModel);

                        Common.currentOrder.setOrderStatus(convertStringToStatus);

                        disposable.add(myRestaurantAPI.getToken(Common.API_KEY,
                                Common.currentOrder.getOrderFBID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tokenModel -> {

                            if (tokenModel.isSuccess()){
                                Map<String,String> messageSend = new HashMap<>();
                                messageSend.put(Common.NOTIFI_TITLE,"Your order has been updated");
                                messageSend.put(Common.NOTIFI_CONTENT,new StringBuilder("Your Order")
                                .append(Common.currentOrder.getOrderId())
                                .append(" has been update to")
                                .append(Common.convertStatusToString(Common.currentOrder.getOrderStatus())).toString());
                               // Toast.makeText(getApplication(), ""+tokenModel.getResult().get(0).getToken(), Toast.LENGTH_SHORT).show();
                                FCMSendData fcmSendData = new FCMSendData(tokenModel.getResult().get(0).getToken(),messageSend);

                                disposable.add(ifcmService.sendNotification(fcmSendData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {
                                            Toast.makeText(getApplication(), "Order Updated!", Toast.LENGTH_SHORT).show();
                                        }, throwable -> {
                                            Toast.makeText(getApplication(), "Order Update but can't send Notification", Toast.LENGTH_SHORT).show();
                                        }));


                            }
                        }, throwable -> {
                            Toast.makeText(getApplication(), "[GET TOKEN]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                        }));
                    }, throwable -> {
                        Toast.makeText(getApplication(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        }
        return updateOrderMutableLiveData;
    }
}
