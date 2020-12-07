package com.example.multiplerestaurantsstaffapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multiplerestaurantsstaffapp.Common.Common;
import com.example.multiplerestaurantsstaffapp.Model.Addon;
import com.example.multiplerestaurantsstaffapp.Model.OrderDetail;
import com.example.multiplerestaurantsstaffapp.R;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<OrderDetail> orderDetailList;

    public MyOrderDetailAdapter(Context context, List<OrderDetail> orderDetailList) {
        this.context = context;
        this.orderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewType == 0 ? new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_detail_item, parent, false)) :
                new MyViewHolderAddOn(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_detail_item_addon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        OrderDetail orderDetail = orderDetailList.get(position);
        if (holder instanceof MyViewHolder) {

            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Picasso.get().load(Common.API_RESTAURANT_ENDPOINT+orderDetail.getImage()).into(myViewHolder.img_food_image);
            myViewHolder.txt_food_name.setText(orderDetail.getName());
            myViewHolder.txt_food_quantitiy.setText("Quantity: " + orderDetail.getQuantity());
            myViewHolder.txt_food_size.setText("Size: " + orderDetail.getSize());

        } else {
            if (holder instanceof MyViewHolderAddOn) {
                MyViewHolderAddOn myViewHolder = (MyViewHolderAddOn) holder;
                Picasso.get().load(Common.API_RESTAURANT_ENDPOINT+orderDetail.getImage()).into(myViewHolder.img_food_image);
                myViewHolder.txt_food_name.setText(orderDetail.getName());
                myViewHolder.txt_food_quantitiy.setText("Quantity: " + orderDetail.getQuantity());
                myViewHolder.txt_food_size.setText("Size: " + orderDetail.getSize());

                List<Addon> addons =  new Gson().fromJson(orderDetail.getAddOn(),
                       new TypeToken<List<Addon>>() {
                        }.getType());

                StringBuilder add_on_text = new StringBuilder();
                for (Addon addon : addons) {
                    add_on_text.append(addon.getName()).append("\n");
                    myViewHolder.txt_add_on.setText(add_on_text);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (orderDetailList.get(position).getAddOn().toLowerCase().equals("none") ||
                orderDetailList.get(position).getAddOn().toLowerCase().equals("normal"))
            return 0;
        else
            return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_food_image;
        TextView txt_food_name, txt_food_quantitiy, txt_food_size;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_food_image = itemView.findViewById(R.id.img_food_image);
            txt_food_name = itemView.findViewById(R.id.txt_food_name);
            txt_food_quantitiy = itemView.findViewById(R.id.txt_food_quantitiy);
            txt_food_size = itemView.findViewById(R.id.txt_food_size);

        }
    }

    public class MyViewHolderAddOn extends RecyclerView.ViewHolder {
        ImageView img_food_image;
        TextView txt_food_name, txt_food_quantitiy, txt_food_size, txt_add_on;

        public MyViewHolderAddOn(@NonNull View itemView) {
            super(itemView);
            img_food_image = itemView.findViewById(R.id.img_food_image);
            txt_food_name = itemView.findViewById(R.id.txt_food_name);
            txt_food_quantitiy = itemView.findViewById(R.id.txt_food_quantitiy);
            txt_food_size = itemView.findViewById(R.id.txt_food_size);
            txt_add_on = itemView.findViewById(R.id.txt_add_on);

        }
    }
}
