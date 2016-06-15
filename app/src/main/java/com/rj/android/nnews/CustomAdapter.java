package com.rj.android.nnews;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class CustomAdapter extends ArrayAdapter {
    public CustomAdapter(Context context, String[] text ) {
        super(context, R.layout.list_item_layout,text);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater= LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.list_item_layout, parent, false);
        Bitmap theBitmap = null;
        TextView textView = (TextView)customView.findViewById(R.id.text);
        ImageView imageView = (ImageView)customView.findViewById(R.id.image);
        Glide.with(getContext())
                .load("https://static01.nyt.com/images/2016/04/09/us/09stevemiller/09-miller-mediumThreeByTwo440.jpg")
                .fitCenter()
                .placeholder(R.drawable.loading)
                .centerCrop()
                .into(imageView);


        textView.setText("1");

        return customView;
    }
}
