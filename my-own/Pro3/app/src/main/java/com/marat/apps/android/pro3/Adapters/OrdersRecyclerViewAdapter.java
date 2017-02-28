package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marat.apps.android.pro3.Activities.OrderDetailsActivity;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.R;

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;
    private CWStationsDatabase db;

    public OrdersRecyclerViewAdapter(Cursor data, Context c, CWStationsDatabase database) {
        cursor = data;
        context = c;
        db = database;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView carWashName;
        private TextView carWashAddress;
        private TextView orderServices;
        private TextView orderDate;
        private TextView orderPrice;
        private TextView orderStatus;
        private ItemClickListener listener;

        ViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            carWashName = (TextView) itemView.findViewById(R.id.moStationNameTextView);
            carWashAddress = (TextView) itemView.findViewById(R.id.moStationAddressTextView);
            orderServices = (TextView) itemView.findViewById(R.id.moOrderServiceTextView);
            orderDate = (TextView) itemView.findViewById(R.id.moOrderDateTextView);
            orderPrice = (TextView) itemView.findViewById(R.id.moOrderPriceTextView);
            orderStatus = (TextView) itemView.findViewById(R.id.moOrderStatus);
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
                db.open();
                cursor.moveToPosition(position);
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("row_id", cursor.getLong(cursor.getColumnIndex(CWStationsDatabase.ROW_ID)));
                db.close();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.open();
        cursor.moveToPosition(position);
        holder.carWashName.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_NAME)));
        holder.carWashAddress.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_ADDRESS)));
        holder.orderServices.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_USER_ORDER_SERVICES)));
        holder.orderDate.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_USER_ORDER_DATE)));
        holder.orderPrice.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_USER_ORDER_PRICE)) + " тг.");
        holder.orderStatus.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_USER_ORDER_STATUS)));

        if ("Активный".equals(holder.orderStatus.getText().toString())) {
            holder.orderStatus.setBackgroundResource(R.drawable.bg_order_status_active);
        } else {
            holder.orderStatus.setBackgroundResource(R.drawable.bg_order_status_ended);
        }
        db.close();
    }

    @Override
    public int getItemCount() {
        db.open();
        int count = cursor.getCount();
        db.close();
        return count;
    }

}
