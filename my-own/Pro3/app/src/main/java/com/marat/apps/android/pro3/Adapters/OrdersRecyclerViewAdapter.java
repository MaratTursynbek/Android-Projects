package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marat.apps.android.pro3.Activities.OrderDetailsActivity;
import com.marat.apps.android.pro3.Models.Order;
import com.marat.apps.android.pro3.R;

import java.util.ArrayList;

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Order> ordersArrayList;

    public OrdersRecyclerViewAdapter(Context context, ArrayList<Order> ordersArrayList) {
        this.context = context;
        this.ordersArrayList = ordersArrayList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView carWashName;
        private TextView orderServices;
        private TextView orderDate;
        private TextView orderPrice;
        private TextView orderStatus;
        private ItemClickListener listener;

        ViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            carWashName = (TextView) itemView.findViewById(R.id.ocStationNameTextView);
            orderServices = (TextView) itemView.findViewById(R.id.ocOrderServiceTextView);
            orderDate = (TextView) itemView.findViewById(R.id.ocOrderDateTextView);
            orderPrice = (TextView) itemView.findViewById(R.id.ocOrderPriceTextView);
            orderStatus = (TextView) itemView.findViewById(R.id.ocOrderStatus);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }

        interface ItemClickListener {
            void onItemClick(int position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_order_card, parent, false);
        return new ViewHolder(v, new ViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("order_id", ordersArrayList.get(position).getOrderID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = ordersArrayList.get(position);
        holder.carWashName.setText(order.getCarWashName());
        holder.orderServices.setText(order.getOrderCarType() + ": " + order.getOrderServices());
        holder.orderDate.setText(order.getOrderTime());
        holder.orderPrice.setText(order.getOrderPrice());
        holder.orderStatus.setText(order.getOrderStatus());

        if ("Активный".equals(order.getOrderStatus())) {
            holder.orderStatus.setBackgroundResource(R.drawable.bg_order_status_active);
        } else {
            holder.orderStatus.setBackgroundResource(R.drawable.bg_order_status_ended);
        }
    }

    @Override
    public int getItemCount() {
        return ordersArrayList.size();
    }

    public void updateCursor(ArrayList<Order> ordersArrayList) {
        this.ordersArrayList = ordersArrayList;
        notifyDataSetChanged();
    }
}
