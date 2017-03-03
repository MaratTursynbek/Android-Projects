package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marat.apps.android.pro3.Activities.CWStationDetailsActivity;
import com.marat.apps.android.pro3.Databases.CWStationsDatabase;
import com.marat.apps.android.pro3.R;

public class CarWashersFavoriteRecyclerViewAdapter extends RecyclerView.Adapter<CarWashersFavoriteRecyclerViewAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;
    private CWStationsDatabase db;
    private String origin = "";
    private int currentCityId = -1;

    public CarWashersFavoriteRecyclerViewAdapter(Cursor data, Context c, CWStationsDatabase database, String origin) {
        cursor = data;
        context = c;
        db = database;
        this.origin = origin;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView carWashPhoto;
        private TextView carWashName;
        private TextView carWashAddress;
        private TextView carWashPrice;
        private TextView carWashDistance;
        private ItemClickListener listener;
        private View cardView;
        private RelativeLayout parentLayout;
        private View headerLayout;
        private TextView header;

        ViewHolder(View itemView, ItemClickListener listener) {
            super(itemView);
            carWashName = (TextView) itemView.findViewById(R.id.carWashNameTextView);
            carWashAddress = (TextView) itemView.findViewById(R.id.carWashAddressTextView);
            carWashPrice = (TextView) itemView.findViewById(R.id.carWashPriceTextView);
            carWashDistance = (TextView) itemView.findViewById(R.id.carWashDistanceToTextView);
            cardView = (View) itemView.findViewById(R.id.listItemCarWashCardView);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.listItemCarWashParentLayout);
            headerLayout = (View) itemView.findViewById(R.id.listItemCarWashHeaderLayout);
            header = (TextView) itemView.findViewById(R.id.listItemCarWashHeaderTextView);
            this.listener = listener;
            cardView.setOnClickListener(this);
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
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_cw_station_card_header, parent, false);
        return new ViewHolder(v, new ViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                db.open();
                cursor.moveToPosition(position);
                Intent intent = new Intent(context, CWStationDetailsActivity.class);
                intent.putExtra("rowId", cursor.getLong(cursor.getColumnIndex(CWStationsDatabase.ROW_ID)));
                intent.putExtra("origin", origin);
                db.close();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("logtag", "onBindViewHolder: " + position);
        db.open();
        cursor.moveToPosition(position);
        holder.carWashName.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_NAME)));
        holder.carWashAddress.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_ADDRESS)));
        holder.carWashPrice.setText("Кузов + Салон от " + cursor.getInt(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_EXAMPLE_PRICE)) + " тг.");

        int nextId = cursor.getInt(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_CITY_ID));
        Log.d("logtag", "currentId = " + currentCityId + " and nextId = " + nextId);

        if ( (position == 0) || (currentCityId != cursor.getInt(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_CITY_ID)))) {
            Log.d("logtag", "header is shown");
            holder.header.setText(cursor.getString(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_CITY_NAME)));
            currentCityId = cursor.getInt(cursor.getColumnIndex(CWStationsDatabase.KEY_CAR_WASH_CITY_ID));
        } else {
            Log.d("logtag", "header is deleted");
            holder.parentLayout.removeView(holder.headerLayout);
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

    public void updateCursor(Cursor newCursor) {
        cursor = newCursor;
        //currentCityId = -1;
        notifyDataSetChanged();
        Log.d("logtag", "notifyDataSetChanged");
    }
}
