package com.example.multiplerestaurantsstaffapp.Common;

import com.example.multiplerestaurantsstaffapp.Model.Order;
import com.example.multiplerestaurantsstaffapp.Model.RestaurantOwner;

public class Common {
    public static String SERVER_IP;
    public static String API_RESTAURANT_ENDPOINT = "http://" + Common.SERVER_IP + ":3000/";
    public static String API_RESTAURANT_Payment_ENDPOINT = "http://" + Common.SERVER_IP + ":3001/";
    public static final String API_KEY = "1234";

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
}
