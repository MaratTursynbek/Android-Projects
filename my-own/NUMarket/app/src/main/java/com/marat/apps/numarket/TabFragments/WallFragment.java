package com.marat.apps.numarket.TabFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marat.apps.numarket.Adapters.WallAdapter;
import com.marat.apps.numarket.Model.Post;
import com.marat.apps.numarket.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WallFragment extends Fragment {

    public static final String TAG = "myTag";

    ArrayList<Post> allPosts;
    int totalCount;
    int numPosts = 0;
    String allAuthorIds = "";
    ArrayList<String> allAuthorNames;
    String jsonDataPosts;

    LoadDataTask loadDataTask;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemAnimator itemAnimator;
    WallAdapter adapter;
    TextView emptyTextView;
    ProgressBar loadingProgressBar;
    Button tryLoadingDataAgainButton;

    public WallFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v1 = inflater.inflate(R.layout.fragment_wall, container, false);
        Log.v(TAG, "onCreateView");

        recyclerView = (RecyclerView) v1.findViewById(R.id.recycler_view);
        emptyTextView = (TextView) v1.findViewById(R.id.empty_text);
        loadingProgressBar = (ProgressBar) v1.findViewById(R.id.progressBar);
        tryLoadingDataAgainButton = (Button) v1.findViewById(R.id.tryLoadingDataAgainButton);

        setProgressBarVisible();
        allPosts = new ArrayList<Post>();
        allAuthorNames = new ArrayList<String>();
        itemAnimator = new DefaultItemAnimator();
        layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);

                Log.v(TAG, "onLayoutChildren");

                if (findFirstCompletelyVisibleItemPosition() == allPosts.size() - 1) {
                    Log.v(TAG, "isLastItem");
                    loadDataTask.execute();
                    adapter.updatePosts(allPosts);
                }
                else {
                    Log.v(TAG, "is Not Last Item");
                }
            }
        };

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        loadDataTask = new LoadDataTask();

        return v1;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        loadDataTask.execute();
    }

    public void setProgressBarVisible() {
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);
        tryLoadingDataAgainButton.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    public void setRecyclerViewVisible() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        tryLoadingDataAgainButton.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.GONE);
    }

    public void setErrorViewsVisible() {
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        tryLoadingDataAgainButton.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.GONE);
    }

    class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.v(TAG, "before doInBackground");
            getDataInBackground();
            Log.v(TAG, "after doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (allPosts.isEmpty()) {
                Log.v(TAG, "allPosts is Empty");
                setErrorViewsVisible();
            } else {
                Log.v(TAG, "allPosts is not empty");
                adapter = new WallAdapter(allPosts, getActivity());
                recyclerView.setAdapter(adapter);
                setRecyclerViewVisible();
            }
        }
    }

    private void getDataInBackground() {

        VKRequest request = VKApi.wall().get(VKParameters.from(VKApiConst.OWNER_ID, "-100177655", VKApiConst.COUNT, "20", VKApiConst.OFFSET, numPosts + ""));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                if (Looper.myLooper() == Looper.getMainLooper()) {
                    Log.v(TAG, "Main Thread");
                } else {
                    Log.v(TAG, "NOT Main Thread");
                }

                jsonDataPosts = response.responseString;
                Log.v(TAG, "json is ready");
            }

            @Override
            public void onError(VKError error) {
                Log.v(TAG, "VKRequest: onError");
                setErrorViewsVisible();
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Log.v(TAG, "VKRequest: attemptFailed");
                setErrorViewsVisible();
            }
        });

        try {
            Log.v(TAG, "before parsing");
            parsePostsData(jsonDataPosts);
            Log.v(TAG, "after parsing");
        } catch (JSONException e) {
            Log.v(TAG, "EXCEPTION is thrown");
            e.printStackTrace();
        }

        VKRequest request2 = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, allAuthorIds));
        request2.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                String jsonDataUsers = response.responseString;

                JSONObject data = null;
                JSONArray authors = null;
                JSONObject author = null;
                try {
                    data = new JSONObject(jsonDataUsers);
                    authors = data.getJSONArray("response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (authors != null) {
                    for (int i=0; i < authors.length(); i++) {

                        try {
                            author = authors.getJSONObject(i);
                            allAuthorNames.get(i).setAuthorName(author.getString("first_name") + " " + author.getString("last_name"));
                            Log.v(TAG, author.getString("first_name") + " " + author.getString("last_name"));
                        } catch (JSONException e) {
                            Log.v(TAG, "EXCEPTION is thrown inside of onComplete in request2");
                            e.printStackTrace();
                        }

                    }
                }
                else {
                    Log.v(TAG, "authors is null");
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Log.v(TAG, "attemptFailed");
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.v(TAG, "onError: " + error.toString());
            }
        });
    }

    private void parsePostsData(String jsonData) throws JSONException {
        JSONObject data = new JSONObject(jsonData);
        JSONObject response = data.getJSONObject("response");
        JSONArray items = response.getJSONArray("items");
        totalCount = Integer.parseInt(response.getString("count"));

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.v(TAG, "Main Thread");
        } else {
            Log.v(TAG, "NOT Main Thread");
        }

        Log.v(TAG, "before loop");

        for (int i = 0; i < items.length(); i++) {

            JSONObject jsonPost = items.getJSONObject(i);
            JSONObject likes = jsonPost.getJSONObject("likes");
            JSONObject comments = jsonPost.getJSONObject("comments");
            JSONObject reposts = jsonPost.getJSONObject("reposts");

            allAuthorIds = allAuthorIds + jsonPost.getString("from_id") + ", ";

            final Post post = new Post();

            post.setPostId(jsonPost.getString("id"));
            post.setAuthorId(jsonPost.getString("from_id"));
            post.setTime(Long.parseLong(jsonPost.getString("date")));
            post.setBodyText(jsonPost.getString("text"));
            post.setNumberOfLikes(likes.getString("count"));
            post.setNumberOfComments(comments.getString("count"));
            post.setNumberOfReposts(reposts.getString("count"));

            numPosts++;
            allPosts.add(post);
            Log.v(TAG, "post number " + i + " is added to list");
        }
    }
}