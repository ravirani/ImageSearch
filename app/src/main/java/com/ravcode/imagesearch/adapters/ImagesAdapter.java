package com.ravcode.imagesearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ravcode.imagesearch.R;
import com.ravcode.imagesearch.models.Image;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ravi on 9/18/14.
 */
public class ImagesAdapter extends ArrayAdapter<Image> {

    private static class ViewHolder {
        TextView tvTitle;
        ImageView ivImage;
    }

    public ImagesAdapter(Context context, List<Image> objects) {
        super(context, R.layout.grid_image, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Image image = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.grid_image, parent, false);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.ivImage.setImageResource(0);
        Picasso.with(getContext()).load(image.thumbnailURL).into(viewHolder.ivImage);
        viewHolder.tvTitle.setText(image.title);
        return convertView;
    }
}

