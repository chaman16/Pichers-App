package com.example.pichers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

public class gallery extends AppCompatActivity {
Button galleryButton;
Button cameraButton;
Database db;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
           if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
               getPhoto();
           }
        }
        if(requestCode==2){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openCamera();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        db=new Database(this);
        galleryButton=findViewById(R.id.gallery);
        cameraButton=findViewById(R.id.camera);


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void  getPhoto(View view){
        //        permission
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            getPhoto();
        }

    }

    private void getPhoto() {
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if(resultCode!=RESULT_CANCELED) {
            Uri selectedImage = data.getData();
            if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
                try {
                    Intent intent = new Intent(getApplicationContext(), grid.class);
                    db.addData(selectedImage);

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            Toast.makeText(this, "Please choose the file", Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera(View view){
//        PERMISSION
        if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},2);
        }else {
            openCamera();
        }


    }
    public void openCamera(){
        Intent intent=new Intent(this.getApplicationContext(),camerActivity.class);
        startActivity(intent);
    }

//    pichers
    public void photo(View view){
        Intent intent=new Intent(this.getApplicationContext(),grid.class);
        startActivity(intent);
    }
}