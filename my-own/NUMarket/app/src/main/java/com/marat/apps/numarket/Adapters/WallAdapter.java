package com.marat.apps.numarket.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marat.apps.numarket.Model.Post;
import com.marat.apps.numarket.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.WallViewHolder> {

    public static final String TAG = "myTag";

    private ArrayList<Post> allPosts;
    private Context context;

    public WallAdapter(ArrayList<Post> data, Context c) {
        allPosts = data;
        context = c;
    }

    @Override
    public WallAdapter.WallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
        WallViewHolder holder = new WallViewHolder(v, context, new WallViewHolder.MyClickListener() {
            @Override
            public void startEventDetailsActivity(Context c, int p) {
                /*
                Intent i = new Intent(c.getApplicationContext(), EventInformationActivity.class);
                int id = Integer.parseInt(mDays.get(p).get(0));
                i.putExtra("PostId", id);
                c.startActivity(i);
                */
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final WallViewHolder holder, int position) {

        holder.authorName.setText(allPosts.get(position).getAuthorName());
        holder.bodyText.setText(allPosts.get(position).getBodyText());

        // Likes
        if ("0".equals(allPosts.get(position).getNumberOfLikes()))
            holder.numberOfLikes.setText("");
        else
            holder.numberOfLikes.setText(allPosts.get(position).getNumberOfLikes());

        // Comments
        if ("0".equals(allPosts.get(position).getNumberOfComments()))
            holder.numberOfComments.setText("");
        else
            holder.numberOfComments.setText(allPosts.get(position).getNumberOfComments());

        // Reposts
        if ("0".equals(allPosts.get(position).getNumberOfReposts()))
            holder.numberOfReposts.setText("");
        else
            holder.numberOfReposts.setText(allPosts.get(position).getNumberOfReposts());

    }

    @Override
    public int getItemCount() {
        return allPosts.size();
    }

    public static class WallViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        MyClickListener listener;

        TextView authorName;
        TextView postTime;
        TextView bodyText;
        TextView numberOfLikes;
        TextView numberOfComments;
        TextView numberOfReposts;

        public WallViewHolder(View itemView, Context c, MyClickListener listen) {
            super(itemView);
            context = c;
            listener = listen;

            authorName = (TextView) itemView.findViewById(R.id.authorName);
            postTime = (TextView) itemView.findViewById(R.id.timeOfPost);
            bodyText = (TextView) itemView.findViewById(R.id.bodyText);
            numberOfLikes = (TextView) itemView.findViewById(R.id.numberOfLikesTextView);
            numberOfComments = (TextView) itemView.findViewById(R.id.numberOfCommentsTextView);
            numberOfReposts = (TextView) itemView.findViewById(R.id.numberOfRepostsTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.startEventDetailsActivity(context, this.getLayoutPosition());
        }

        public interface MyClickListener {
            void startEventDetailsActivity(Context c, int p);
        }
    }

    public void updatePosts(ArrayList<Post> newData) {
        allPosts = newData;
        notifyDataSetChanged();
    }
}
