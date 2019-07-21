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
import android.widget.RelativeLayout;
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

public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>(); //이미지경로들을 담을 곳(이미지경로들을 알아야.. Storage에 그 경로들을 가지고 올릴 수가 있다.)
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private ImageView selectedImageView; //이미지뷰를 선택했을 때 전역으로 저장하는 곳
    private  EditText selectedEditText;
    private int pathCount, successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.videoModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);
        findViewById(R.id.contentsEditText).setOnFocusChangeListener(onFocusChangeListener);
        findViewById(R.id.titleEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    selectedEditText=null;
                }
            }
        });
    }

    @Override       //일딴 ,,,,,,,,, 값을 확인하는곳?  받는 곳?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {      //MemberInitActivity.java에 myStartActivity메소드가  보낸 결과를 받는 곳
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:                                                        //myStartActivity메소드에서 requestCode를 0으로 보낸다고 해서
                if (resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    if(selectedEditText ==null){   //null(Text가 없을경우)일때는 그냥 편소처럼 아래로 추가되게하고
                        parent.addView(linearLayout);
                    }else {
                        for(int i =0; i< parent.getChildCount(); i++){
                            if(parent.getChildAt(i)==selectedEditText.getParent()){    //현재 selected의 위치
                                parent.addView(linearLayout,i+1);   //현재 selected의 위치에서 그다음에 만든다는 뜻이다
                                break;
                            }
                        }
                    }
                                     ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView=(ImageView)v;  //선택이 되었을 때 전역으로 저장하는 곳

                        }
                    });
                    Glide.with(this).load(profilePath).override(1000).into(imageView); //image리사이징(외부 라이브러리)
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).override(1000).into(selectedImageView);  //selectedImageView(선택한 이미지뷰만 바꿔주면 되겠찌?)
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    myStartActivity(GalleryActivity.class, "image",0);
                    break;
                case R.id.video:
                    myStartActivity(GalleryActivity.class, "video",0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleryActivity.class, "video",1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.videoModify:
                    myStartActivity(GalleryActivity.class, "video",1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    parent.removeView((View)selectedImageView.getParent()); //getParent()하면 부모뷰에 접근을 한다.
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;

            }
        }
    };

    View.OnFocusChangeListener onFocusChangeListener=new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                selectedEditText = (EditText)v;
            }
        }
    };


    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();


        if (title.length() > 0) {
            final ArrayList<String> contentsList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //firestore 초기화
            final DocumentReference documentReference = firebaseFirestore.collection("posts").document();

            for (int i = 0; i < parent.getChildCount(); i++) {   //안에 자식 뷰만큼 반복을 할거다
                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i); //자식뷰먼저 저장해주고!!(parent(부모) 안에 있는 뷰에 하나하나 접근을 할거다)
                for(int ii=0;ii<linearLayout.getChildCount();ii++){
                    View view =linearLayout.getChildAt(ii);
                    if (view instanceof EditText) { //★view가 만약에 EditText가 있으면 이게 실행이 되야된다.
                        String text = ((EditText)view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                        }
                    } else {
                        contentsList.add(pathList.get(pathCount));
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + ".jpg");
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();    //API에 사용 법 있음.StorageMetadata
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e("로그", "uri : " + uri);  //사진 -> uri
                                            contentsList.set(index, uri.toString());
                                            successCount++;
                                            if (pathList.size() == successCount) {
                                                //완료
                                                WriteInfo writeInfo = new WriteInfo(title, contentsList, user.getUid(), new Date());    //user.getUid()  = 로그인한 사용자
                                                storeUpload(documentReference, writeInfo);
                                                for (int a = 0; a < contentsList.size(); a++) {
                                                    Log.e("로그", "콘텐츠 : " + contentsList.get(a));
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러 : " + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_LONG).show();
        }
    }


    private void storeUpload(DocumentReference documentReference, WriteInfo writeInfo) {
        documentReference.set(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void myStartActivity(Class c, String media,int requestCode) {
        Intent intent = new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("media", media); //값 전달
        startActivityForResult(intent, requestCode);  //사진 찍은 거 . 결과 받아야하니깐 startActivityForResult 사용함
    }

}
