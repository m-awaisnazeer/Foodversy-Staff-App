
package com.example.multiplerestaurantsstaffapp.Retrofit;



import com.example.multiplerestaurantsstaffapp.Model.RestaurantOwnerModel;
import com.example.multiplerestaurantsstaffapp.Model.UpdateRestaurantOwnerModel;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
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
