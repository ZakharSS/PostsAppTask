package com.iit.zakhar.postapp;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iit.zakhar.postapp.model.GridItems;
import com.iit.zakhar.postapp.viewholder.PostViewHolder;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private final static String POSTID_EXTRAS = "id";
    private ArrayList<GridItems> objects;
    Context context;

    public GridAdapter(Context context, ArrayList<GridItems> items) {
        this.context = context;
        objects = items;
    }


    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        view.setOnClickListener(new MyOnItemClickListener());

        PostViewHolder viewHolder = new PostViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        GridItems gridItems = objects.get(position);
        holder.postId.setText(gridItems.getId());
        holder.title.setText(gridItems.getTitle());

    }

    @Override
    public int getItemCount() {
        if (objects != null) {
            return objects.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (objects != null && position >= 0 && position < getItemCount()) {
            return Integer.parseInt(objects.get(position).getId());
        }
        return 0;
    }

    private class MyOnItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView postId = (TextView) v.findViewById(R.id.postID);
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(POSTID_EXTRAS, postId.getText());
            context.startActivity(intent);
        }
    }
}
