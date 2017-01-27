package com.marat.apps.android.pro3.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marat.apps.android.pro3.Databases.AllCarWashersDatabase;
import com.marat.apps.android.pro3.R;

public class FavoriteStationsAdapter extends RecyclerView.Adapter<FavoriteStationsAdapter.FavoritesViewHolder> {

    private Cursor cursor;
    private Context context;
    private AllCarWashersDatabase db;

    public FavoriteStationsAdapter(Cursor data, Context c, AllCarWashersDatabase database) {
        cursor = data;
        context = c;
        db = database;
    }

    static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        private ImageView carWashPhoto;
        private TextView carWashName;
        private TextView carWashAddress;
        private TextView carWashPrice;
        private TextView carWashDistance;
        private View item;

        public FavoritesViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            carWashName = (TextView) itemView.findViewById(R.id.carWashNameTextView);
            carWashAddress = (TextView) itemView.findViewById(R.id.carWashAddressTextView);
            carWashPrice = (TextView) itemView.findViewById(R.id.carWashPriceTextView);
            carWashDistance = (TextView) itemView.findViewById(R.id.carWashDistanceToTextView);
        }
    }

    @Override
    public FavoriteStationsAdapter.FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.car_wash_station_card_view, parent, false);
        return new FavoriteStationsAdapter.FavoritesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FavoriteStationsAdapter.FavoritesViewHolder holder, int position) {
        final int pos = position;
        db.open();
        cursor.moveToPosition(position);
        holder.carWashName.setText(cursor.getString(cursor.getColumnIndex(AllCarWashersDatabase.KEY_NAME)));
        holder.carWashAddress.setText(cursor.getString(cursor.getColumnIndex(AllCarWashersDatabase.KEY_ADDRESS)));
        holder.carWashPrice.setText("Кузов + Салон от " + cursor.getInt(cursor.getColumnIndex(AllCarWashersDatabase.KEY_PRICE)) + " тг.");
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("tag", "item " + pos + " being clicked");
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
