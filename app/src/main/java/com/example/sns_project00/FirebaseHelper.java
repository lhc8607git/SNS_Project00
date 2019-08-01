package com.example.sns_project00;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sns_project00.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.sns_project00.Util.isStorageUrl;
import static com.example.sns_project00.Util.showToast;
import static com.example.sns_project00.Util.storageUrlToName;

public class FirebaseHelper {
    private Activity activity;
    private OnPostListener onPostListener;
    private int successCount;

    public FirebaseHelper(Activity activity){
        this.activity=activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener=onPostListener;
    }

    public void storageDelete(final PostInfo postInfo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =  storage.getReference();

        final String id=postInfo.getId();
        Log.e("로그","삭제"+id);
        ArrayList<String> contentsList = postInfo.getContents();
        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if (isStorageUrl(contents)) {  //1.URL인지를 검사 하는 방법 && 2.URL 경로가 맞는지 검사
                successCount++;
                StorageReference desertRef = storageRef.child("posts/"+id+"/"+storageUrlToName(contents));
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(id,postInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(activity,"ERROR");

                    }
                });

            }
        }
        storeDelete(id, postInfo);
    }



    private void storeDelete(final String id,final PostInfo postInfo){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //firestore 초기화
        if(successCount==0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity,"게시글을 삭제하였습니다.");
                            onPostListener.onDelete(postInfo);
                           // PostUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(activity,"게시글을 삭제하지 못했습니다.");
                        }
                    });
        }
    }
}
