package com.bphc.dbs_project.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageHelper {

    public final static int IMAGE_PICK = 1001;
    public final static int IMAGE_CLICK = 1002;
    public static String path;

    public static Bitmap getBitmapFromUri(int requestCode, Intent data, Context context) {
        Bitmap bitmap = null;
        switch (requestCode) {
            case IMAGE_PICK:
                if (data.getData() != null) {
                    Uri returnUri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), returnUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case IMAGE_CLICK:
                if (data.getExtras() != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                }
                break;
        }
        return bitmap;
    }

    public static String getBase64FormBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
