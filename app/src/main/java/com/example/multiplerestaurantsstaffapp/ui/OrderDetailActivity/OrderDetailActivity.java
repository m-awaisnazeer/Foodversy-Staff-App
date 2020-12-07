package com.example.multiplerestaurantsstaffapp.ui.OrderDetailActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.multiplerestaurantsstaffapp.Adapter.MyOrderDetailAdapter;
import com.example.multiplerestaurantsstaffapp.Common.Common;
import com.example.multiplerestaurantsstaffapp.Interface.DataLoadListner;
import com.example.multiplerestaurantsstaffapp.Model.Status;
import com.example.multiplerestaurantsstaffapp.R;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class OrderDetailActivity extends AppCompatActivity {

    OrderDetailViewModel viewModel;
    AlertDialog dialog;
    DataLoadListner dataLoadListner;

    Toolbar toolbar;
    TextView txt_order_number;
    AppCompatSpinner spinner_status;
    RecyclerView recycler_order_detail;


    @Override
    protected void onDestroy() {
        viewModel.onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);


        init();
        initView();

        dataloadingListnerdeomViewModel();

        loadOrderDetail();

    }

    private void dataloadingListnerdeomViewModel() {
        dataLoadListner = new DataLoadListner() {
            @Override
            public void onLoadSuccess() {
                if (dialog == null) {
                    init();
                } else {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
                Toast.makeText(OrderDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadFailed(String message) {
                if (dialog == null) {
                    init();
                } else {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
                Toast.makeText(OrderDetailActivity.this, "[GET Order Detail]" + message, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.order_detail));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        txt_order_number = findViewById(R.id.txt_order_number);
        spinner_status = findViewById(R.id.spinner_status);
        recycler_order_detail = findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_order_detail.setLayoutManager(linearLayoutManager);
        recycler_order_detail.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));

        txt_order_number.setText(new StringBuilder("Order Number: #").append(Common.currentOrder.getOrderId()));
        initStatusSpinner();


    }

    private void loadOrderDetail() {

        dialog.show();
        viewModel.loadOrderDetails(dataLoadListner).observe(this, orderDetailModel -> {
            if (orderDetailModel.isSuccess()) {

                if (orderDetailModel.getResult().size() > 0) {

                    MyOrderDetailAdapter adapter = new MyOrderDetailAdapter(this,orderDetailModel.getResult());
                    recycler_order_detail.setAdapter(adapter);
                }
            } else {

            }
        });

    }

    private void initStatusSpinner() {
        List<Status> statusList = new ArrayList<Status>();


        statusList.add(new Status(0, "Placed")); //index 0
        statusList.add(new Status(1, "Shipping"));//index 1
        statusList.add(new Status(2, "Shipped"));//index 2
        statusList.add(new Status(-1, "Cancelled"));//index 3

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_status.setAdapter(adapter);
        spinner_status.setSelection(Common.convertStatusToIndex(Common.currentOrder.getOrderStatus()));
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_detail_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_save) {
            Toast.makeText(this, "Save test", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}