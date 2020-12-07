package com.example.multiplerestaurantsstaffapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.multiplerestaurantsstaffapp.Common.Common;
import com.example.multiplerestaurantsstaffapp.Interface.ILoadMore;
import com.example.multiplerestaurantsstaffapp.Model.Order;
import com.example.multiplerestaurantsstaffapp.R;
import com.example.multiplerestaurantsstaffapp.ui.OrderDetailActivity.OrderDetailActivity;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_ITEM_ITEM = 1;
    Context context;
    List<Order> orders;
    SimpleDateFormat simpleDateFormat;

    RecyclerView recyclerView;
    ILoadMore iLoadMore;

    boolean isLoading = false;
    int totalItemCount = 0, lastVisibleItem = 0, visibleThreshold = 10;

    public MyOrderAdapter(Context context, List<Order> orders, RecyclerView recyclerView) {
        this.context = context;
        this.orders = orders;
        this.recyclerView = recyclerView;
        simpleDateFormat = new SimpleDateFormat("MM/DD/YYYY");
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) ;
                {
                    if (iLoadMore != null)
                        iLoadMore.OnLoadMore();
                    isLoading = true;
                }
            }
        });
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void addItem(List<Order> addItems) {
        int startInsertedIndex = orders.size();
        orders.addAll(addItems);
        notifyItemInserted(startInsertedIndex);
    }

    public void setiLoadMore(ILoadMore iLoadMore) {
        this.iLoadMore = iLoadMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (orders.get(position) == null) // if we meet 'null' value in List, we will understand this is loading state
            return VIEW_TYPE_LOADING;
        else
            return VIEW_ITEM_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_ITEM_ITEM) {
            itemView = (LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order, parent, false));
            return new ViewHolder(itemView);
        } else {
            itemView = (LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false));
            return new MyLoadingHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Order orderItem = orders.get(position);


        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.txt_order_number.setText("Order Number :#" + String.valueOf(orderItem.getOrderId()));
            viewHolder.txt_order_processing.setText(Common.convertStatusToString(orderItem.getOrderStatus()));
            viewHolder.txt_order_phone.setText(orderItem.getOrderPhone());
            viewHolder.txt_order_address.setText(orderItem.getOrderAddress());
            viewHolder.txt_order_date.setText(simpleDateFormat.format(orderItem.getOrderDate()));
            if (orderItem.getTotalPrice() != null) {
                viewHolder.txt_order_total_price.setText(context.getString(R.string.money_sign) + orderItem.getTotalPrice().toString());
            }
            viewHolder.txt_num_of_item.setText("Num of Items:  " + orderItem.getNumOfItem());

            if (orderItem.isCod())
                viewHolder.txt_payment_method.setText(new StringBuilder("Cash On Delivery"));
            else
                viewHolder.txt_payment_method.setText(new StringBuilder("TransID:").append(orderItem.getTransactionId()));


            viewHolder.setListner((itemView, position1) -> {
                Common.currentOrder = orderItem;
                context.startActivity(new Intent(context,OrderDetailActivity.class));
            });
        } else if (holder instanceof MyLoadingHolder) {
            MyLoadingHolder viewHolder = (MyLoadingHolder) holder;

            viewHolder.progress_bar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_order_number, txt_order_processing, txt_order_phone, txt_order_address, txt_payment_method, txt_order_date, txt_order_total_price, txt_num_of_item;

        IRecyclerItemClickListner listner;

        public void setListner(IRecyclerItemClickListner listner) {
            this.listner = listner;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_order_number = itemView.findViewById(R.id.txt_order_number);
            txt_order_processing = itemView.findViewById(R.id.txt_order_processing);
            txt_order_phone = itemView.findViewById(R.id.txt_order_phone);
            txt_order_address = itemView.findViewById(R.id.txt_order_address);
            txt_payment_method = itemView.findViewById(R.id.txt_payment_method);
            txt_order_date = itemView.findViewById(R.id.txt_order_date);
            txt_order_total_price = itemView.findViewById(R.id.txt_order_total_price);
            txt_num_of_item = itemView.findViewById(R.id.txt_num_of_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listner.onClickListner(v, getAdapterPosition());
        }
    }

    public class MyLoadingHolder extends RecyclerView.ViewHolder {
        ProgressBar progress_bar;

        public MyLoadingHolder(@NonNull View itemView) {
            super(itemView);
            progress_bar = itemView.findViewById(R.id.progress_bar);


        }
    }
}
