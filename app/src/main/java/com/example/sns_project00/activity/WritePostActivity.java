package com.example.sns_project00.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.sns_project00.R;
import com.example.sns_project00.WriteInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class WritePostActivity extends BasicActivity{
    private static final String TAG="WritePostActivity";
    private FirebaseUser user;
    private ArrayList<String> pathList= new ArrayList<>(); //이미지경로들을 담을 곳(이미지경로들을 알아야.. Storage에 그 경로들을 가지고 올릴 수가 있다.)
    private LinearLayout parent;
    private int pathCount, successCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent=findViewById(R.id.contentsLayout);

        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
    }

    @Override       //일딴 ,,,,,,,,, 값을 확인하는곳?  받는 곳?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {      //MemberInitActivity.java에 myStartActivity메소드가  보낸 결과를 받는 곳
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0 :{                                                                  //myStartActivity메소드에서 requestCode를 0으로 보낸다고 해서
                if(resultCode == Activity.RESULT_OK){
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(this).load(profilePath).override(1000).into(imageView); //image리사이징(외부 라이브러리)
                    parent.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    parent.addView(editText);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    myStartActivity(GalleryActivity.class,"image");
                    break;
                case R.id.video:
                    myStartActivity(GalleryActivity.class,"video");
                    break;
            }
        }
    };


    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.contentsEditText)).getText().toString();


        if (title.length() > 0 && contents.length()>0) {
            final ArrayList<String> contentsList=new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            for(int i=0; i<parent.getChildCount(); i++){   //안에 자식 뷰만큼 반복을 할거다
                View view=parent.getChildAt(i); //자식뷰먼저 저장해주고!!(parent(부모) 안에 있는 뷰에 하나하나 접근을 할거다)
                if(view instanceof EditText){ //view가 만약에 EditText이면
                    String text= ((EditText)view).getText().toString();
                    if(text.length()>0){
                        contentsList.add(text);
                    }
                }else {
                    contentsList.add(pathList.get(pathCount));
                    final StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/"+pathCount+".jpg");   //★★사용자의 Uid 가져와서 각각 넣음(구별)★★
                    try{
                        InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+pathCount).build();    //API에 사용 법 있음.StorageMetadata
                        UploadTask uploadTask = mountainImagesRef.putStream(stream,metadata);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final int index=Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.e("로그","uri : "+uri);  //사진 -> uri
                                        contentsList.set(index,uri.toString());
                                        successCount++;
                                        if(pathList.size()==successCount){
                                            //완료
                                            WriteInfo writeInfo = new WriteInfo(title,contentsList,user.getUid(),new Date());    //user.getUid()  = 로그인한 사용자
                                            storeUpload(writeInfo);
                                            for(int a=0; a<contentsList.size();a++){
                                                Log.e("로그","콘텐츠 : "+contentsList.get(a));
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }catch (FileNotFoundException e){
                        Log.e("로그","에러 : "+e.toString());
                    }
                    pathCount++;
                }
            }
        }else {
            Toast.makeText(getApplicationContext(), "회원정보를 입력해주세요.", Toast.LENGTH_LONG).show();
        }
    }



    private void storeUpload(WriteInfo writeInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance(); //firestore 초기화
        db.collection("posts").add(writeInfo)     //firestore에 추가(add)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                        }
            });
    }

    private void myStartActivity(Class c,String media){
        Intent intent=new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("media",media); //값 전달
        startActivityForResult(intent,0);  //사진 찍은 거 . 결과 받아야하니깐 startActivityForResult 사용함
    }

}
