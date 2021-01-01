package com.example.multiplerestaurantsstaffapp.Retrofit;



import com.example.multiplerestaurantsstaffapp.Model.FCMResponse;
import com.example.multiplerestaurantsstaffapp.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAOARSLmk:APA91bGs97hXGy7fhtDp6HMM8JZ8hNYLwwONhI2H57gp5nnNCw64POK-5dlJVbzqWlDx54FTWbr0ZGjzzGm-nkIcqimtarXLs3cOc1gZtbWpXoU086_jnZl0mBJfo18p9CSmh_Uci2BT"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
