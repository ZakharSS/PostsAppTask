package com.iit.zakhar.postapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.iit.zakhar.postapp.model.GridItems;

import java.util.ArrayList;


public class PostsGrid extends Fragment {

    private final static String GRID_ITEMS = "grid_items";
    private ArrayList<GridItems> itemsList;
    RecyclerView mRecyclerView;
    GridLayoutManager gridLayoutManager;


    static PostsGrid newInstance(ArrayList<GridItems> gridItems) {
        PostsGrid gridFragment = new PostsGrid();
        Bundle arguments = new Bundle();
        arguments.putSerializable(GRID_ITEMS, gridItems);
        gridFragment.setArguments(arguments);
        return gridFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsList = (ArrayList<GridItems>) getArguments().getSerializable(GRID_ITEMS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_grid, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getContext() != null) {
            GridAdapter gridAdapter = new GridAdapter(getContext(), itemsList);
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(gridAdapter);
            }
        }
    }
}
