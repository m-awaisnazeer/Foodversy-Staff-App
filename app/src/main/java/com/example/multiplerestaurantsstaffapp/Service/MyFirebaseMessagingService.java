package com.example.multiplerestaurantsstaffapp.Service;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.multiplerestaurantsstaffapp.Common.Common;
import com.example.multiplerestaurantsstaffapp.Retrofit.IMyRestaurantAPI;
import com.example.multiplerestaurantsstaffapp.Retrofit.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
      IMyRestaurantAPI myRestaurantAPI;
      CompositeDisposable compositeDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        compositeDisposable = new CompositeDisposable();
        Paper.init(this);

    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);

        String fbid = Paper.book().read(Common.REMEMBER_FBID);
        String apikey = Paper.book().read(Common.API_KEY_TAG);

        compositeDisposable.add(myRestaurantAPI.updateTokenToServer(apikey, fbid, newToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tokenModel -> {

                }, throwable -> {
                    Toast.makeText(this, "[REFRESH TOKEN]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> dataRecv = remoteMessage.getData();
        if (dataRecv != null) {

            Common.showNotification(this,new Random().nextInt(),
                    dataRecv.get(Common.NOTIFI_TITLE),
                    dataRecv.get(Common.NOTIFI_CONTENT),
                    null);
        }
    }
}
