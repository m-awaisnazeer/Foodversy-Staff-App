package com.example.multiplerestaurantsstaffapp.Common;

import com.example.multiplerestaurantsstaffapp.Model.RestaurantOwner;

public class Common {
    public static String SERVER_IP;
    public static String API_RESTAURANT_ENDPOINT = "http://" + Common.SERVER_IP + ":3000/";
    public static String API_RESTAURANT_Payment_ENDPOINT = "http://" + Common.SERVER_IP + ":3001/";
    public static final String API_KEY = "1234";

    public  static RestaurantOwner currentRestaurantOwner;
}
