package com.reactlibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Utilities {
    public static class Permissions {

        private static String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        private static boolean permissionGranted(Activity activity, String... permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
            return true;
        }

        public static boolean checkAllPermissionsGranted(Activity activity) {
            boolean allPermissionsGranted = permissionGranted(activity, PERMISSIONS);
            if (!allPermissionsGranted) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS, 1);
            }
            return allPermissionsGranted;
        }
    }

    public static class Files {
        public static String saveBitmap(Context context, Bitmap bitmap, float compressionQuality, String fileName) throws IOException{
            File newPhotoFile;
            OutputStream outStream;
            String parentDir = new ContextWrapper(context).getFilesDir().getAbsolutePath() + "/photos";
            if (!new File(parentDir).exists()){
                new File(parentDir).mkdir();
            }
            newPhotoFile = new File(parentDir + "/" + fileName + ".png");
            outStream = new FileOutputStream(newPhotoFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, (int) compressionQuality * 100, outStream);
            outStream.close();
            return newPhotoFile.getAbsolutePath();
        }
    }



}