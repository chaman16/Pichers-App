package com.example.pichers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.VideoCapture;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class camerActivity extends AppCompatActivity {
    Button btnClose, btnLens, btnVideo, btnStop, btnPhoto;
    private Executor executor = Executors.newSingleThreadExecutor();
    CameraSelector cameraSelector;
    CameraView mCameraView;
    Database db=new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camer);

        btnPhoto = findViewById(R.id.btnPhoto);
        btnVideo = findViewById(R.id.btnVideo);
        btnStop = findViewById(R.id.btnStop);
        btnLens = findViewById(R.id.btnLens);
        btnClose = findViewById(R.id.btnClose);

        mCameraView = (CameraView)findViewById(R.id.view_finder);
        mCameraView.setFlash(ImageCapture.FLASH_MODE_AUTO);

        ImageCapture.Builder builder = new ImageCapture.Builder();
        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        // if has hdr (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable hdr.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCameraView.bindToLifecycle(camerActivity.this);
    }


//    capture image

    public void photo(View view) {
        if (mCameraView.isRecording()) {
            return;
        }

        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        final File file1 = new File(getBatchDirectoryName(), mDateFormat.format(new Date()) + ".jpg");

        mCameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file1).build();
        mCameraView.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                new Handler(Looper.getMainLooper()).post(() -> galleryAddPic(file1, 0));
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {

            }
        });
    }

    public String getBatchDirectoryName() {
        String app_folder_path;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            app_folder_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();

        } else {
            app_folder_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        }
        Log.i("getBatchDirectoryName: ", app_folder_path);
        Log.i("getBatchDirectoryName: ", String.valueOf(Build.VERSION.SDK_INT));

        return app_folder_path;
    }

    private void galleryAddPic(File originalFile, int mediaType) {
        if (!originalFile.exists()) {
            return;
        }

//        int pathSeparator = String.valueOf(originalFile).lastIndexOf('/');
//        int extensionSeparator = String.valueOf(originalFile).lastIndexOf('.');
//        String filename = pathSeparator >= 0 ? String.valueOf(originalFile).substring(pathSeparator + 1) : String.valueOf(originalFile);
//        String extension = extensionSeparator >= 0 ? String.valueOf(originalFile).substring(extensionSeparator + 1) : "";


//        String mimeType = extension.length() > 0 ? MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ENGLISH)) : null;

        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.TITLE, filename);
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
//        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
//        Log.i( "galleryAddPic: ",values.toString());
//        if (mimeType != null && mimeType.length() > 0)
//            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
//
        Uri externalContentUri;
        if (mediaType == 0)
            externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        else if (mediaType == 1)
            externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        else
            externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Log.i( "galleryAddPic: ",externalContentUri.toString());
        // Android 10 restricts our access to the raw filesystem, use MediaStore to save media in that case
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera");
            values.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.MediaColumns.IS_PENDING, true);
            Log.i("galleryAddPicwwwww: ", values.toString());
            Uri uri = getContentResolver().insert(externalContentUri, values);
            Log.i("galleryAddPic: uri", uri.toString());
            if (uri != null) {
                try {
                    if (WriteFileToStream(originalFile, getContentResolver().openOutputStream(uri))) {
                        values.put(MediaStore.MediaColumns.IS_PENDING, false);
                        getContentResolver().update(uri, values, null, null);
                        Intent intent = new Intent(this.getApplicationContext(), grid.class);
                        db.addData(uri);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    getContentResolver().delete(uri, null, null);
                }
            }
            originalFile.delete();
        } else {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(originalFile));
            sendBroadcast(mediaScanIntent);
        }


    } //gallery add end

    private static boolean WriteFileToStream(File file, OutputStream out) {
        try {
            InputStream in = new FileInputStream(file);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    //Log.e( "Unity", "Exception:", e );
                }
            }
        } catch (Exception e) {
            //Log.e( "Unity", "Exception:", e );
            return false;
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                //Log.e( "Unity", "Exception:", e );
            }
        }
        return true;
    } //write end


    public void close(View view) {
        if (mCameraView.isRecording()) {
            mCameraView.stopRecording();
        }
        finish();
    }

    public void stop(View view) {
        if (mCameraView.isRecording()) {
            mCameraView.stopRecording();
        }
    }

    @SuppressLint("RestrictedApi")
    public void vedio(View view) {
        if(mCameraView.isRecording()){return;}

        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date()) + ".mp4");

        mCameraView.setCaptureMode(CameraView.CaptureMode.VIDEO);
        VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(file).build();
           mCameraView.startRecording(outputFileOptions, executor, new VideoCapture.OnVideoSavedCallback() {
               @Override
               public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                   galleryAddPic(file,1);
               }

               @Override
               public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {

               }
           });


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView.isRecording()) {
            mCameraView.stopRecording();
        }
        finish();
    }
    public void lens(View view) {
        if (mCameraView.isRecording()) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (mCameraView.hasCameraWithLensFacing(CameraSelector.LENS_FACING_FRONT)) {
            mCameraView.toggleCamera();
        } else {
            return;
        }
    }
}