package com.example.sns_project00;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

import java.net.URLConnection;

public class Util {
    public Util(){  }

    //path
    public static final String INTENT_PATH="path";

    //media
    public static final String INTENT_MEDIA="media";

    //이미지, 비디오 정의
    public static final int GALLERY_IMAGE=0;
    public static final int GALLERY_VIDEO=1;

    //토스트
    public static void showToast(Activity activity,String msg){
        Toast.makeText(activity,msg,Toast.LENGTH_LONG).show();
    }

    //url 위치 , 맞는지
    public static boolean isStorageUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/sns-project00.appspot.com/o/posts");
    }


    public static String storageUrlToName(String url){
//        String[] list =url.split("\\?");
//        String[] list2= list[0].split("%2F");
//        String name = list2[list2.length-1];

        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2F").length-1];
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
}
