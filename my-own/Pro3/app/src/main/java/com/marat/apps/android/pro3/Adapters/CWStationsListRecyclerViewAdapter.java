package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marat.apps.android.pro3.Activities.CWStationDetailsActivity;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.R;

public class CWStationsListRecyclerViewAdapter extends RecyclerView.Adapter<CWStationsListRecyclerViewAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;
    private CWStationsDatabase db;

    public CWStationsListRecyclerViewAdapter(Cursor data, Context c, CWStationsDatabase database) {
        cursor = data;
        context = c;
        db = database;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView carWashPhoto;
        private TextView carWashName;
        private TextView carWashAddress;
        private TextView carWashPrice;
        private TextView carWashDistance;
        private View item;

        ViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            carWashName = (TextView) itemView.findViewById(R.id.carWashNameTextView);
            carWashAddress = (TextView) itemView.findViewById(R.id.carWashAddressTextView);
            carWashPrice = (TextView) itemView.findViewById(R.id.carWashPriceTextView);
            carWashDistance = (TextView) itemView.findViewById(R.id.carWashDistanceToTextView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_cw_station_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        db.open();
        cursor.moveToPosition(pos);
        holder.carWashName.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_NAME)));
        holder.carWashAddress.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_ADDRESS)));
        holder.carWashPrice.setText("Кузов + Салон от " + cursor.getInt(cursor.getColumnIndex(CWStationsDatabase.KEY_PRICE)) + " тг.");
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CWStationDetailsActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        db.open();
        int count = cursor.getCount();
        db.close();
        return count;
    }
}
