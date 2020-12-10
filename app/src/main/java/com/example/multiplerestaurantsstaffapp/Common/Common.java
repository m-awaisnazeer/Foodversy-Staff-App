package com.example.multiplerestaurantsstaffapp.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.multiplerestaurantsstaffapp.Model.Order;
import com.example.multiplerestaurantsstaffapp.Model.RestaurantOwner;
import com.example.multiplerestaurantsstaffapp.R;

public class Common {
    public static final String NOTIFI_TITLE = "title";
    public static final String NOTIFI_CONTENT = "content";
    public static String SERVER_IP;
    public static String API_RESTAURANT_ENDPOINT = "http://" + Common.SERVER_IP + ":3000/";
    public static String API_RESTAURANT_Payment_ENDPOINT = "http://" + Common.SERVER_IP + ":3001/";
    public static final String API_KEY = "1234";
    public static final String REMEMBER_FBID = "REMEMBER_FBID";
    public static final String API_KEY_TAG = "API_KEY";
    public  static RestaurantOwner currentRestaurantOwner;
    public static Order currentOrder;

    public static String convertStatusToString(int orderStatus) {

        switch (orderStatus) {
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "Shipped";
            case -1:
            default:
                return "Cancelled";
        }
    }

    public static int convertStatusToIndex(int orderStatus) {

        if (orderStatus == -1){
            return 3;
        }else
            return orderStatus;
    }
    public static void showNotification(Context context, int notiId, String title, String body, Intent intent) {
        PendingIntent pendingIntent = null;

        if (pendingIntent != null)
            pendingIntent = PendingIntent.getActivity(context, notiId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "foodversy_my_restaurant";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Foodversy Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Foodversy Client App");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));


        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        notificationManager.notify(notiId, notification);

    }

}
