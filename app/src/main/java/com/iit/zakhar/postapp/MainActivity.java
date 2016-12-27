package com.iit.zakhar.postapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.iit.zakhar.postapp.model.GridItems;
import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private PageIndicator indicator;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private ProgressDialog pDialog;
    private CoordinatorLayout coordinatorLayout;
    private static String TAG = MainActivity.class.getSimpleName();
    private String urlJsonPosts = "http://jsonplaceholder.typicode.com/posts";
    private HashMap<Integer, ArrayList<GridItems>> itemsList;
    private ArrayList<GridItems> gridData;
    private GridItems item;
    private LogsSaving logsSaving;
    int numberOfPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.clmain);


        Button btnSaveLogs = (Button) findViewById(R.id.btnSaveLogs);
        btnSaveLogs.setOnClickListener(this);
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (PageIndicator) findViewById(R.id.pagerIndicator);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        checkConnection();
    }

    @Override
    protected void onResume() {
        AppController.getInstance().setConnectivityListener(this);
        super.onResume();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = getResources().getString(R.string.internet_connection);
            Log.d(TAG, message);
            getNumberOfPages();
        } else {
            message = getResources().getString(R.string.no_connection);

            final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.retry_action, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                    checkConnection();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }
    }

    public void getNumberOfPages() {
        if (!pDialog.isShowing()) pDialog.show();
        itemsList = new HashMap<>();
        gridData = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(urlJsonPosts,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            // Parsing json array response
                            // loop through each json object
                            int maxId = 0;
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject post = (JSONObject) response.get(i);

                                if (Integer.parseInt(post.getString("id")) > maxId) {
                                    maxId = Integer.parseInt(post.getString("id"));
                                }
                                String id = post.getString("id");
                                String title = post.getString("title");
                                item = new GridItems(id, title);
                                gridData.add(item);
                            }

                            int count = 0;
                            int keyCount = 0;
                            itemsList.put(keyCount, new ArrayList<GridItems>());

                            for (int i = 0; i < gridData.size(); i++) {
                                itemsList.get(keyCount).add(gridData.get(i));
                                count++;
                                if (count == 6) {
                                    keyCount++;
                                    itemsList.put(keyCount, new ArrayList<GridItems>());
                                    count = 0;
                                }
                            }

                            double pages = Math.ceil(maxId / 6.0);
                            numberOfPages = (int) pages;
                            pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), itemsList);
                            pager.setAdapter(pagerAdapter);
                            indicator.setViewPager(pager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            VolleyLog.d(TAG, "Error: " + e.getMessage());
                        }
                        if (pDialog.isShowing()) pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (pDialog.isShowing()) pDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveLogs:
                if (isExternalStorageWritable()) {
                    logsSaving = new LogsSaving();
                    logsSaving.execute();
                } else
                    Toast.makeText(this, R.string.external_storage_error, Toast.LENGTH_LONG).show();
        }
    }

    private class LogsSaving extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pDialog.isShowing()) pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            LogCat logCat = new LogCat();
            return logCat.saveLogcatToFile(getBaseContext());
        }

        @Override
        protected void onPostExecute(String filePath) {
            super.onPostExecute(filePath);
            if (pDialog.isShowing()) pDialog.dismiss();
            Toast.makeText(getBaseContext(), "File saved in " + filePath, Toast.LENGTH_LONG).show();
            logsSaving = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (logsSaving != null) {
            logsSaving.cancel(false);
        }
        super.onDestroy();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private HashMap<Integer, ArrayList<GridItems>> gridItems;

        public MyPagerAdapter(FragmentManager fm, HashMap<Integer, ArrayList<GridItems>> gridItems) {
            super(fm);
            this.gridItems = gridItems;
        }

        @Override
        public Fragment getItem(int position) {
            return PostsGrid.newInstance(gridItems.get(position));
        }

        @Override
        public int getCount() {
            return numberOfPages;
        }
    }
}

