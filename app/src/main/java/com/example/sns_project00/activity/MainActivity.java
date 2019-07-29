package com.example.sns_project00.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;
import com.example.sns_project00.adapter.MainAdapter;
import com.example.sns_project00.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import static com.example.sns_project00.Util.isStorageUrl;
import static com.example.sns_project00.Util.showToast;
import static com.example.sns_project00.Util.storageUrlToName;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRef;
    private MainAdapter mainAdapter;
    private  ArrayList<PostInfo> postList;
    private int successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("ㅁㄴㅇㄹ");  //액션바 이름름

       //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //android 화면 (세로 모드로 고정)
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();//중복되는 곳이 있어서 --------- (그냥,참고,,, 사용자의 UID를 사용할려면 이 코드 사용 해야함)
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        if(firebaseUser==null){   //★ 현재 로그인된 유저가 있는지 없는지 확인 하는 부분 (만약,로그인된 유저가 없으면 로그인 화면으로 이동)---  대박 이거 지리네 로그인이 유지되네 다시 들어와도 그대로네!!! //로그인 유지??할 수 있는 거 같은데??
            myStartActivity(SignUpActivity.class);
        }else {  //사용자 정보 작성 안 했으면 보이고, 작성 했으면 안보이고 설정해줌.
            firebaseFirestore = FirebaseFirestore.getInstance(); //firestore 초기화
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null){
                            if (document.exists()) {     //여기가 데이터가 성공적으로 오는부분
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        postList = new ArrayList<>();
        //String[] myDataset={"강아지","고양이","드래곤","치킨"};   //임시사용
        mainAdapter = new MainAdapter(MainActivity.this,postList);     //myDataset이게 GalleryAdapter.java에 가보면 ArrayList<String>로 대어 있음.
        mainAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView =findViewById(R.id.recyclerView);
        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(mainAdapter);
    }

    @Override
    protected void onResume(){  //(액티비티가 재실행 되거나, 다시 왔을 때) 이것을 사용한다.
        super.onResume();
        PostUpdate();
    }


    OnPostListener onPostListener=new OnPostListener() {
        @Override
        public void onDelete(int position) {
            final String id=postList.get(position).getId();
            Log.e("로그","삭제"+id);
            ArrayList<String> contentsList = postList.get(position).getContents();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) {  //1.URL인지를 검사 하는 방법 && 2.URL 경로가 맞는지 검사
                    successCount++;
                    StorageReference desertRef = storageRef.child("posts/"+id+"/"+storageUrlToName(contents));
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            successCount--;
                            storeUploader(id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            showToast(MainActivity.this,"ERROR");

                        }
                    });

                }
            }
            storeUploader(id);
        }

        @Override
        public void onModify(int position) {
            myStartActivity(WritePostActivity.class,postList.get(position));
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                /*
                case R.id.btnmainlogout:
                    FirebaseAuth.getInstance().signOut();   //★ Firebase Auth 로그아웃 하는 방법
                    myStartActivity(SignUpActivity.class);
                    break;
                */
                case R.id.floatingActionButton:
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }
    };

    private void PostUpdate(){
        if(firebaseUser!=null){
            CollectionReference collectionReference=firebaseFirestore.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>)document.getData().get("contents"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId()));
                                    Log.e("로그","데이터 : "+document.getData().get("title").toString());
                                }
                                mainAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void storeUploader(String id){
        if(successCount==0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          showToast(MainActivity.this,"게시글을 삭제하였습니다.");
                            PostUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                          showToast(MainActivity.this,"게시글을 삭제하지 못했습니다.");
                        }
                    });
        }
    }

    private void myStartActivity(Class c){
        Intent intent=new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //뒤로가기 할때 깨끗하게
        startActivity(intent);
    }
    private void myStartActivity(Class c,PostInfo postInfo){
        Intent intent=new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("postInfo",postInfo);
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //뒤로가기 할때 깨끗하게
        startActivity(intent);
    }
}
