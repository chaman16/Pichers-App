package com.example.pichers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Uri> uri;
    public  ImageAdapter(Context mContext, ArrayList<Uri> uri){
        this.mContext=mContext;
        this.uri=uri;
    }
    @Override
    public int getCount() {
        return uri.size();
    }

    @Override
    public Object getItem(int i) {
        return uri.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.i("getView: ",String.valueOf(i));
        ImageView imageView=new ImageView(mContext);

        Glide.with(imageView).load(uri.get(i)).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(340,350));
        return imageView;
    }
}
