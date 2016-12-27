package com.iit.zakhar.postapp.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iit.zakhar.postapp.R;

public class PostViewHolder extends RecyclerView.ViewHolder{

    public TextView postId;
    public TextView title;

    public PostViewHolder(View itemView) {
        super(itemView);
        postId = (TextView) itemView.findViewById(R.id.postID);
        title = (TextView) itemView.findViewById(R.id.postTitle);
    }
}
