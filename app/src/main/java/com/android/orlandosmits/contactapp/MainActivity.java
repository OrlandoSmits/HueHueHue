package com.android.orlandosmits.contactapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    ListView mHueListView;
    HueAdapter mHueAdapter;
    ArrayList<Hue> hueArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

       GETRequest();
//
        mHueListView = (ListView) findViewById(R.id.hueListView);

        //Koppelen van de list
        mHueAdapter = new HueAdapter(this,
                getLayoutInflater(),
                hueArrayList);
        Log.i("log",hueArrayList.toString());
        mHueListView.setAdapter(mHueAdapter);
        mHueListView.setOnItemClickListener(this);

        mHueAdapter.notifyDataSetChanged();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_deleteDB:


                mHueAdapter.notifyDataSetChanged();

                return true;

            case R.id.action_addPerson:
                GETRequest();
                mHueAdapter.notifyDataSetChanged();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("SelectedItem: ", position + "");
        Intent i = new Intent(getApplicationContext(), DetailActivity.class);
        Hue hue = hueArrayList.get(position);
        i.putExtra("Hue", hue);

        startActivity(i);

    }


    private void getJSON(String response)
    {
        try {
            JSONObject jsonObject = new JSONObject(response);

            for(Iterator<String> iter = jsonObject.keys(); iter.hasNext();) {
                Hue hue = new Hue();
                hue.id = iter.next();
                hue.name = jsonObject.getJSONObject(hue.id).getString("name");
                hue.on = jsonObject.getJSONObject(hue.id).getJSONObject("state").getBoolean("on");
                hue.brightness = jsonObject.getJSONObject(hue.id).getJSONObject("state").getInt("bri");

                hueArrayList.add(hue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mHueAdapter.notifyDataSetChanged();
    }

    public void GETRequest() {
//        String url = "http://192.168.1.249:80/api/newdeveloper/lights";
        String url = "http://192.168.1.179/api/80b8a9620291a47fec92fa34484f5b/lights";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        getJSON(response);
                        Log.i("response", response.toString());
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getRandomUser", error.toString());
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}