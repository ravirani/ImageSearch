package com.ravcode.imagesearch.activities;

import com.ravcode.imagesearch.activities.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.ravcode.imagesearch.R;
import com.squareup.picasso.Picasso;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ImageActivity extends Activity {
    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Read intent parameters
        String fullURL = getIntent().getStringExtra(ImagesSearchActivity.IMAGE_URL_INTENT_KEY);
        int imageWidth = getIntent().getIntExtra(ImagesSearchActivity.IMAGE_WIDTH_INTENT_KEY, 0);
        int imageHeight = getIntent().getIntExtra(ImagesSearchActivity.IMAGE_HEIGHT_INTENT_KEY, 0);

        // Load view
        final ImageView fullScreenImageView = (ImageView)findViewById(R.id.ivImageFullScreen);

        // Calculate image dimensions
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // Calculate new width and height while keeping the same aspect ratio
        int width = dm.widthPixels;

        // Loose last-minute check to make sure we don't get divide by zero error
        // TODO: Add proper messaging and handling for this scenario
        if (imageWidth == 0) {
            imageWidth = width;
        }
        int height = width * imageHeight / imageWidth;
        Picasso.with(this).load(fullURL).resize(width, height).into(fullScreenImageView);


        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, fullScreenImageView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.hide();

        // Set up the user interaction to manually show or hide the system UI.
        fullScreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });
    }
}
