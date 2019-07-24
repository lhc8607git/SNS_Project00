package com.example.sns_project00;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

public class Util {
    public Util(){  }

    public static void showToast(Activity activity,String msg){
        Toast.makeText(activity,msg,Toast.LENGTH_LONG).show();
    }
    public static boolean isStorageUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/sns-project00.appspot.com/o/posts");
    }

    public static String storageUrlToName(String url){
//        String[] list =url.split("\\?");
//        String[] list2= list[0].split("%2F");
//        String name = list2[list2.length-1];

        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2F").length-1];
    }
}
