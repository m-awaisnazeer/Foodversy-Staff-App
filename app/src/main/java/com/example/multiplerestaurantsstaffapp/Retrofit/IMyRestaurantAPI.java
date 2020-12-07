
package com.example.multiplerestaurantsstaffapp.Retrofit;


import com.example.multiplerestaurantsstaffapp.Model.MaxOrderModel;
import com.example.multiplerestaurantsstaffapp.Model.OrderModel;
import com.example.multiplerestaurantsstaffapp.Model.RestaurantOwnerModel;
import com.example.multiplerestaurantsstaffapp.Model.UpdateRestaurantOwnerModel;
import com.example.multiplerestaurantsstaffapp.Model.OrderDetailModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyRestaurantAPI {

    /*
             ############# GET ################
  */


    @GET("restaurantowner")
    Observable<RestaurantOwnerModel> getRestaurantOwner(@Query("key") String key,
                                                        @Query("fbid") String fbid);

    @GET("orderbyrestaurant")
    Observable<OrderModel> getOrder(@Query("key") String key,
                                    @Query("restaurantId") String restaurantId,
                                    @Query("from") int from,
                                    @Query("to") int to);


    @GET("maxorderbyrestaurant")
    Observable<MaxOrderModel> getMaxOrder(@Query("key") String key,
                                          @Query("restaurantId") String restaurantId);


    @GET("orderdetailbyrestaurant")
    Observable<OrderDetailModel> getOrderDetail(@Query("key") String key,
                                                   @Query("orderId") int orderId);

    /*
        ########### POST #############
     */

    @POST("restaurantowner")
    @FormUrlEncoded
    Observable<UpdateRestaurantOwnerModel> updateRestaurantOwnerModel(@Field("key") String key,
                                                                      @Field("userPhone") String userPhone,
                                                                      @Field("userName") String userName,
                                                                      @Field("fbid") String fbid);
}
