package com.example.multiplerestaurantsstaffapp.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multiplerestaurantsstaffapp.Adapter.MyOrderAdapter;
import com.example.multiplerestaurantsstaffapp.Interface.ILoadMore;
import com.example.multiplerestaurantsstaffapp.Interface.IMaxOrderListner;
import com.example.multiplerestaurantsstaffapp.Model.Order;
import com.example.multiplerestaurantsstaffapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class HomeFragment extends Fragment implements IMaxOrderListner, ILoadMore {

    private HomeViewModel homeViewModel;
    RecyclerView recycler_view;
    AlertDialog alertDialog;
    LayoutAnimationController layoutAnimationController;
    int maxData = 0;
    MyOrderAdapter adapter;
    List<Order> orderList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recycler_view = root.findViewById(R.id.recycler_view);
        alertDialog = new SpotsDialog.Builder().setCancelable(false).setContext(requireContext()).build();

        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        recycler_view.setLayoutManager(manager);
        recycler_view.addItemDecoration(new DividerItemDecoration(requireContext(), manager.getOrientation()));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_item_from_left);

        Toast.makeText(requireContext(), ""+ FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        getMaxOrder();
        return root;
    }

    private void getMaxOrder() {
        alertDialog.show();
        homeViewModel.getMaxOrder().observe(getViewLifecycleOwner(), maxOrderModel -> {

            if (maxOrderModel.isSuccess()) {
                maxData = maxOrderModel.getResult().get(0).getMaxRowNum();
                getAllOrder(0, 10);


            } else {
                Toast.makeText(requireContext(), "" + maxOrderModel.getMessage(), Toast.LENGTH_SHORT).show();

            }
            alertDialog.dismiss();

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeViewModel.onDestroy();
    }


    @Override
    public void onMaxOrderSuccess(String msg) {

    }

    @Override
    public void onMaxOrderFailed(String msg) {

    }

    @Override
    public void OnLoadMore() {
        if (adapter.getItemCount() < maxData) {

            orderList.add(null);
            adapter.notifyItemInserted(orderList.size() - 1);

            getAllOrder(adapter.getItemCount() + 1, adapter.getItemCount() + 10);

            adapter.notifyDataSetChanged();
            adapter.setLoaded();
        } else {
            Toast.makeText(requireContext(), "MAX DATA TO LOAD", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllOrder(int from, int to) {
        homeViewModel.getAllOrder(from, to).observe(getViewLifecycleOwner(), orderModel -> {
            if (orderModel.isSuccess()) {
                if (adapter == null) {
                    orderList = new ArrayList();
                    orderList = orderModel.getResult();
                    adapter = new MyOrderAdapter(requireContext(), orderList, recycler_view);
                    adapter.setiLoadMore(this);

                    recycler_view.setAdapter(adapter);
                    recycler_view.setLayoutAnimation(layoutAnimationController);
                } else {
                    orderList.remove(orderList.size() - 1);
                    orderList = orderModel.getResult();
                    adapter.addItem(orderList);
                }
            } else {
                Toast.makeText(requireContext(), "" + orderModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}