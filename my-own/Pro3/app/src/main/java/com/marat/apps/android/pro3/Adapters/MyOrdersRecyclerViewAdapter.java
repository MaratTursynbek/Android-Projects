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

public class MyOrdersRecyclerViewAdapter extends RecyclerView.Adapter<MyOrdersRecyclerViewAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;
    private CWStationsDatabase db;

    public MyOrdersRecyclerViewAdapter(Cursor data, Context c, CWStationsDatabase database) {
        cursor = data;
        context = c;
        db = database;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView carWashName;
        private TextView carWashAddress;
        private TextView orderServices;
        private TextView orderDate;
        private TextView orderPrice;
        private TextView orderStatus;
        private View item;

        ViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            carWashName = (TextView) itemView.findViewById(R.id.moStationNameTextView);
            carWashAddress = (TextView) itemView.findViewById(R.id.moStationAddressTextView);
            orderServices = (TextView) itemView.findViewById(R.id.moOrderServiceTextView);
            orderDate = (TextView) itemView.findViewById(R.id.moOrderDateTextView);
            orderPrice = (TextView) itemView.findViewById(R.id.moOrderPriceTextView);
            orderStatus = (TextView) itemView.findViewById(R.id.moOrderStatus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_order_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        final long rowId;
        db.open();
        cursor.moveToPosition(pos);
        rowId = Long.parseLong(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_ROW_ID)));
        holder.carWashName.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_NAME)));
        holder.carWashAddress.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_ADDRESS)));
        holder.orderServices.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_ORDER_SERVICES)));
        holder.orderDate.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_ORDER_DATE)));
        holder.orderPrice.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_ORDER_PRICE)) + " тг.");
        holder.orderStatus.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_ORDER_STATUS)));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("row_id", rowId);
                context.startActivity(intent);
            }
        });

        if ("Активный".equals(holder.orderStatus.getText().toString())) {
            holder.orderStatus.setBackgroundResource(R.drawable.bg_order_status_active);
        }
        else {
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
