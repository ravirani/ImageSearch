package com.ravcode.imagesearch.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ravcode.imagesearch.R;
import com.ravcode.imagesearch.adapters.ImagesAdapter;
import com.ravcode.imagesearch.models.Image;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ImagesSearchActivity extends Activity implements SearchView.OnQueryTextListener {

    static String IMAGE_URL_INTENT_KEY = "image_url";
    private static final String GOOGLE_IMAGE_SEARCH_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&q=";
    private StaggeredGridView gvImageResults;
    private ArrayList<Image> images;
    private ImagesAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        setupViews();

        Log.i("INFO", "Coming in onCreate for ImagesSearchActivity");
        // Data Source
        images = new ArrayList<Image>();
        if (savedInstanceState != null) {
            images = (ArrayList<Image>)savedInstanceState.getSerializable("images");
        }

        // Attach the data source to the adapter
        imagesAdapter = new ImagesAdapter(this, images);

        // Link the adapter to the list view
        gvImageResults.setAdapter(imagesAdapter);

        // Do the last search
        performSearch("cat");
    }

    private void setupViews() {
        gvImageResults = (StaggeredGridView)findViewById(R.id.gvImages);
        gvImageResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Image selectedImage = images.get(position);
                Intent intent = new Intent(ImagesSearchActivity.this, ImageActivity.class);
                intent.putExtra(IMAGE_URL_INTENT_KEY, selectedImage.fullURL);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_images_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        return true;
    }

    private void performSearch(String query) {
        // Create the network client
        AsyncHttpClient client = new AsyncHttpClient();

        // Make the GET request
        client.get(GOOGLE_IMAGE_SEARCH_URL + query, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray imageResultsJSON = response.getJSONObject("responseData").getJSONArray("results");
                    imagesAdapter.clear();
                    imagesAdapter.addAll(Image.fromJSONArray(imageResultsJSON));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    public void onSearch(MenuItem item) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("images", images);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        performSearch(s);
        hideSoftKeyboard(getCurrentFocus());
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
