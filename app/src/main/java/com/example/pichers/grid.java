package com.example.pichers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.Inflater;

public class grid extends AppCompatActivity {
  final    ArrayList<Uri> uriList=new ArrayList<>();
      Database db=new Database(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);



        Cursor data=db.getListContent();
        if(data.getCount()==0){
            Toast.makeText(this, "There is no Pichers", Toast.LENGTH_SHORT).show();
        }else {
            while (data.moveToNext()) {
                Uri myUri = Uri.parse(data.getString(1));
                uriList.add(myUri);

            }
        }




        GridView gridView= findViewById(R.id.grid);
        gridView.setAdapter(new ImageAdapter(this,uriList));


    }

    }
