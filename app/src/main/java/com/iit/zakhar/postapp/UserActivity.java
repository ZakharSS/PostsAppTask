package com.iit.zakhar.postapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iit.zakhar.postapp.database.HelperFactory;
import com.iit.zakhar.postapp.database.User;
import com.iit.zakhar.postapp.database.UserDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;


public class UserActivity extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private TextView name;
    private TextView nickName;
    private TextView postID;
    private TextView email;
    private TextView web;
    private TextView phone;
    private TextView city;
    private Button btnSaveUser;
    private CoordinatorLayout coordinatorLayout;
    private String TAG = UserActivity.class.getSimpleName();
    private static final String POSTID_EXTRAS = "id";
    private ProgressDialog pDialog;
    private FillData fillData;
    private String urlJsonPosts = "http://jsonplaceholder.typicode.com/posts";
    private String urlJsonUsers = "http://jsonplaceholder.typicode.com/users/";
    private String postNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cluser);

        name = (TextView) findViewById(R.id.userName);
        nickName = (TextView) findViewById(R.id.userNickName);
        postID = (TextView) findViewById(R.id.postNumber);
        email = (TextView) findViewById(R.id.userEmail);
        web = (TextView) findViewById(R.id.userWeb);
        phone = (TextView) findViewById(R.id.userPhone);
        city = (TextView) findViewById(R.id.userCity);
        btnSaveUser = (Button) findViewById(R.id.btnSaveUser);

        email.setOnClickListener(this);
        web.setOnClickListener(this);
        phone.setOnClickListener(this);
        btnSaveUser.setOnClickListener(this);
        city.setOnClickListener(this);

        Intent intent = getIntent();
        postNum = intent.getStringExtra(POSTID_EXTRAS);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        checkConnection();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private class FillData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pDialog.isShowing())
                pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(String id) {
            super.onPostExecute(id);
            postID.setText(id);
            getUser(id);
            if (pDialog.isShowing()) pDialog.dismiss();
            fillData = null;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.userEmail:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email.getText().toString()));
                startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
                break;
            case R.id.userPhone:
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.getText().toString()));
                startActivity(phoneIntent);
                break;
            case R.id.userWeb:
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + web.getText().toString()));
                startActivity(webIntent);
                break;
            case R.id.userCity:
                getLocation(postID.getText().toString());
                break;
            case R.id.btnSaveUser:
                try {
                    User user = new User();
                    user.name = name.getText().toString();
                    user.nickName = nickName.getText().toString();
                    user.email = email.getText().toString();
                    user.webSite = web.getText().toString();
                    user.phone = phone.getText().toString();
                    user.city = city.getText().toString();
                    if (isExternalStorageWritable()) {
                        UserDAO userDao = HelperFactory.getHelper().getUserDAO();
                        userDao.create(user);
                        Toast.makeText(this, "User " + user.name + " saved", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.external_storage_error, Toast.LENGTH_LONG).show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void getUser(final String postId) {
        JsonArrayRequest request = new JsonArrayRequest(urlJsonPosts,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            // Parsing json array response
                            // loop through each json object
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject post = (JSONObject) response.get(i);

                                String pId = post.getString("id");
                                String UserId = post.getString("userId");
                                if (pId.equals(postId)) {
                                    getUserInfo(urlJsonUsers + UserId);
                                    getSupportActionBar().setTitle("Contact # " + UserId);
                                    break;
                                }
                            }
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
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    public void getUserInfo(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    // Parsing json object response
                    // response will be a json object
                    JSONObject address = response.getJSONObject("address");
                    name.setText(response.getString("name"));
                    nickName.setText(response.getString("username"));
                    email.setText(response.getString("email"));
                    web.setText(response.getString("website"));
                    phone.setText(response.getString("phone"));
                    city.setText(address.getString("city"));
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
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    public void getLocation(final String postId) {
        JsonArrayRequest request = new JsonArrayRequest(urlJsonPosts,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            // Parsing json array response
                            // loop through each json object
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject post = (JSONObject) response.get(i);
                                String pId = post.getString("id");
                                String UserId = post.getString("userId");
                                if (pId.equals(postId)) {
                                    getGeoData(urlJsonUsers + UserId);
                                    break;
                                }
                            }
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
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    public void getGeoData(String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    // Parsing json object response
                    // response will be a json object
                    JSONObject address = response.getJSONObject("address").getJSONObject("geo");
                    String lat = address.getString("lat");
                    String lng = address.getString("lng");

                    Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?z=10");

                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

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
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request);

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
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
            fillData = new FillData();
            fillData.execute(postNum);
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

    @Override
    protected void onDestroy() {
        if (fillData != null) {
            fillData.cancel(false);
        }
        super.onDestroy();
    }
}
