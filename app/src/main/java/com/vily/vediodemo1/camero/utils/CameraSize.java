package com.vily.vediodemo1.camero.utils;

import android.graphics.Point;
import android.util.Log;
import android.util.Size;

import com.vily.vediodemo1.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  * description : 
 *  * Author : Vily
 *  * Date : 2019-11-19
 *  
 **/
public class CameraSize {
    private static final String TAG = "CameraSize";

    public static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];

    }

    public static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();

        for (Size option : choices) {
            Log.i(TAG, "mPreviewSize: ----choice:"+option.getWidth()+"---:"+option.getHeight());

        }
        for (Size option : choices) {

            if(option.getWidth()<1300){
                return option;
            }
        }

        return choices[choices.length-1];
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}
