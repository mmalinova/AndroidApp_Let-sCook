package com.example.letscook.database.typeconverters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class DataConverter {
    public static byte[] imageToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap byteArrayToImage(byte[] array) {
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }
}
