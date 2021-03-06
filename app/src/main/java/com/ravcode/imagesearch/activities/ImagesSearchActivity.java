package com.ravcode.imagesearch.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ravcode.imagesearch.R;
import com.ravcode.imagesearch.activities.util.EndlessScrollListener;
import com.ravcode.imagesearch.adapters.ImagesAdapter;
import com.ravcode.imagesearch.fragments.AdvancedImageFiltersFragment;
import com.ravcode.imagesearch.models.Image;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ImagesSearchActivity extends FragmentActivity implements SearchView.OnQueryTextListener, AdvancedImageFiltersFragment.OnAdvancedImageFiltersSetListener {

    private static final String GOOGLE_IMAGE_SEARCH_URL = "https://ajax.googleapis.com/ajax/services/search/images?";

    static String IMAGE_URL_INTENT_KEY = "image_url";
    static String IMAGE_WIDTH_INTENT_KEY = "image_width";
    static String IMAGE_HEIGHT_INTENT_KEY = "image_height";

    private StaggeredGridView gvImageResults;
    private ArrayList<Image> images;
    private ImagesAdapter imagesAdapter;

    private SearchView mSearchView;
    private String mLastSearchKeyword;
    private String mSize;
    private String mColor;
    private String mType;
    private String mSite;

    private static final String PREF_KEYWORD = "keyword";
    private static final String PREF_SIZE = "size";
    private static final String PREF_COLOR = "color";
    private static final String PREF_TYPE = "type";
    private static final String PREF_SITE = "site";

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

        // Add scroll listener
        addScrollListener();

        // Read preferences & perform default search
        readFromPreferences();
        if (mLastSearchKeyword != null) {
            performSearch(mLastSearchKeyword, 0);
        }
    }

    private void setupViews() {
        gvImageResults = (StaggeredGridView)findViewById(R.id.gvImages);
        gvImageResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Image selectedImage = images.get(position);
                Intent intent = new Intent(ImagesSearchActivity.this, ImageActivity.class);
                intent.putExtra(IMAGE_URL_INTENT_KEY, selectedImage.fullURL);
                intent.putExtra(IMAGE_WIDTH_INTENT_KEY, selectedImage.width);
                intent.putExtra(IMAGE_HEIGHT_INTENT_KEY, selectedImage.height);
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView)searchItem.getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(this);
        if (mLastSearchKeyword != null) {
            searchItem.expandActionView();
            mSearchView.setQuery(mLastSearchKeyword, false);
            mSearchView.clearFocus();
        }

        return true;
    }

    private void performSearch(String query, int page) {

        // Before going any further, first check if we have internet connectivity
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Cannot complete this request. Please check your internet connection or try again later.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create the network client
        AsyncHttpClient client = new AsyncHttpClient();

        // Setup request parameters
        RequestParams requestParams = new RequestParams();

        // Version
        requestParams.put("v", "1.0");

        // Response size
        int responseSize = 8;
        requestParams.put("rsz", responseSize);

        // Keyword
        if (query == null || TextUtils.isEmpty(query)) {
            Toast.makeText(this, "Keyword cannot be empty. Please enter one above.", Toast.LENGTH_LONG).show();
            return;
        }
        requestParams.put("q", query);
        mLastSearchKeyword = query;

        // Size
        if (mSize != null) {
            requestParams.put("imgsz", mSize);
        }

        // Color
        if (mColor != null) {
            requestParams.put("imgcolor", mColor);
        }

        // Type
        if (mType != null) {
            requestParams.put("imgtype", mType);
        }

        // Site Name
        if (mSite != null) {
            requestParams.put("as_sitesearch", mSite);
        }

        // Page
        // The logic here is very much custom to Google search API
        // It only allows the startIndex to be set up to a certain value
        int startIndex = page * 8;
        if (startIndex <= 56) {
            requestParams.put("start", startIndex);
        }
        else {
            return;
        }

        final int requestedPage = page;

        // Make the GET request
        client.get(GOOGLE_IMAGE_SEARCH_URL, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray imageResultsJSON = response.getJSONObject("responseData").getJSONArray("results");
                    if (requestedPage == 0) {
                        imagesAdapter.clear();
                    }
                    imagesAdapter.addAll(Image.fromJSONArray(imageResultsJSON));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Handle the failure and alert the user to retry
                Log.e("ERROR", throwable.toString());
                String errorMessage = "Error completing this request. Details - " + throwable.toString();
                Toast.makeText(ImagesSearchActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Save to preferences
        saveToPreferences();
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
        performSearch(s, 0);
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

    // Advanced search filters Fragment
    public void OnAdvancedImageFiltersSet(String size, String color, String type, String siteName) {
        mSize = size;
        mColor = color;
        mType = type;
        mSite = siteName;

        String keyword = mLastSearchKeyword != null ? mLastSearchKeyword : "";
        performSearch(keyword, 0);
    }

    public void onAdvancedFiltersOpen(MenuItem item) {
        AdvancedImageFiltersFragment filtersFragment = AdvancedImageFiltersFragment.newInstance(mSize, mColor, mType, mSite);
        filtersFragment.show(getSupportFragmentManager(), "fragment_filters");
    }

    private void saveToPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_KEYWORD, mLastSearchKeyword);
        editor.putString(PREF_SIZE, mSize);
        editor.putString(PREF_COLOR, mColor);
        editor.putString(PREF_TYPE, mType);
        editor.putString(PREF_SITE, mSite);
        editor.commit();
    }

    private void readFromPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mLastSearchKeyword = sharedPref.getString(PREF_KEYWORD, null);
        mSize = sharedPref.getString(PREF_SIZE, null);
        mColor = sharedPref.getString(PREF_COLOR, null);
        mType = sharedPref.getString(PREF_TYPE, null);
        mSite = sharedPref.getString(PREF_SITE, null);
    }

    private void addScrollListener() {
        gvImageResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                String keyword = mLastSearchKeyword != null ? mLastSearchKeyword : "";
                performSearch(keyword, page);
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
