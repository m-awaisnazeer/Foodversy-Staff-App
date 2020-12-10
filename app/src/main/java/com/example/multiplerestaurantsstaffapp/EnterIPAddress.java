
package com.example.multiplerestaurantsstaffapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.multiplerestaurantsstaffapp.Common.Common;
import com.example.multiplerestaurantsstaffapp.databinding.ActivityEnterIPAddressBinding;

public class EnterIPAddress extends AppCompatActivity {

    ActivityEnterIPAddressBinding activityEnterIPAddressBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEnterIPAddressBinding = ActivityEnterIPAddressBinding.inflate(getLayoutInflater());

        setContentView(activityEnterIPAddressBinding.getRoot());

        activityEnterIPAddressBinding.submitIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = activityEnterIPAddressBinding.editServerIp.getText().toString();


                if (TextUtils.isEmpty(ip)) {
                    Toast.makeText(EnterIPAddress.this, "Enter IP to continue", Toast.LENGTH_SHORT).show();
                    return;
                }
                Common.SERVER_IP = ip;
                Common.API_RESTAURANT_ENDPOINT = "http://" + ip + ":3000/";
                startActivity(new Intent(EnterIPAddress.this, StartActivity.class));
                finish();
            }
        });

        activityEnterIPAddressBinding.continueWithOldIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.API_RESTAURANT_ENDPOINT = "http://192.168.1.103:3000/";
                Common.API_RESTAURANT_Payment_ENDPOINT = "http://192.168.1.103:3001/";
                startActivity(new Intent(EnterIPAddress.this, StartActivity.class));
                finish();
            }
        });
    }
}