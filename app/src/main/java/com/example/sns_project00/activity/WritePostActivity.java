package com.example.sns_project00.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;
import com.example.sns_project00.view.ContentsItemView;
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

import static com.example.sns_project00.Util.GALLERY_IMAGE;
import static com.example.sns_project00.Util.GALLERY_VIDEO;
import static com.example.sns_project00.Util.INTENT_MEDIA;
import static com.example.sns_project00.Util.INTENT_PATH;
import static com.example.sns_project00.Util.isImageFile;
import static com.example.sns_project00.Util.isStorageUrl;
import static com.example.sns_project00.Util.isVideoFile;
import static com.example.sns_project00.Util.showToast;
import static com.example.sns_project00.Util.storageUrlToName;

public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>(); //이미지경로들을 담을 곳(이미지경로들을 알아야.. Storage에 그 경로들을 가지고 올릴 수가 있다.)
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageView; //이미지뷰를 선택했을 때 전역으로 저장하는 곳
    private  EditText selectedEditText;
    private  EditText contentsEditText;
    private  EditText titleEditText;
    private PostInfo postInfo;
    private int pathCount, successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        setToolbarTitle("게시글 작성"); //액션바 이름

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout=findViewById(R.id.loaderLayout);
        contentsEditText=findViewById(R.id.contentsEditText);
        titleEditText=findViewById(R.id.titleEditText);

        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.videoModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(onFocusChangeListener);
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    selectedEditText=null;
                }
            }
        });
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        postInfo=(PostInfo)getIntent().getSerializableExtra("postInfo");
        postInit();
    }

    @Override       //일딴 ,,,,,,,,, 값을 확인하는곳?  받는 곳?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {      //MemberInitActivity.java에 myStartActivity메소드가  보낸 결과를 받는 곳
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:                                                        //myStartActivity메소드에서 requestCode를 0으로 보낸다고 해서
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    /*     -->ContentsItemView.java에가가 만듬
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    */

                    ContentsItemView contentsItemView=new ContentsItemView(this);

                    if(selectedEditText ==null){   //null(Text가 없을경우)일때는 그냥 편소처럼 아래로 추가되게하고
                        parent.addView(contentsItemView);
                    }else {
                        for(int i =0; i< parent.getChildCount(); i++){
                            if(parent.getChildAt(i)==selectedEditText.getParent()){    //현재 selected의 위치
                                parent.addView(contentsItemView,i+1);   //현재 selected의 위치에서 그다음에 만든다는 뜻이다
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(path);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView=(ImageView)v;  //선택이 되었을 때 전역으로 저장하는 곳

                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);

                    /*
                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView=(ImageView)v;  //선택이 되었을 때 전역으로 저장하는 곳

                        }
                    });
                    Glide.with(this).load(path).override(1000).into(imageView); //image리사이징(외부 라이브러리)
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);
                    */
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View)selectedImageView.getParent())-1,path);//이 뷰가 parent의 몇번째에 있는 자식인지 알아온다
                    Glide.with(this).load(path).override(1000).into(selectedImageView);  //selectedImageView(선택한 이미지뷰만 바꿔주면 되겠찌?)
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
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE,0);
                    break;
                case R.id.video:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO,0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE,1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.videoModify:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO,1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    final View selectedView = (View)selectedImageView.getParent();

                    StorageReference desertRef = storageRef.child("posts/"+postInfo.getId()+"/"+storageUrlToName(pathList.get(parent.indexOfChild(selectedView)-1)));
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(WritePostActivity.this,"파일을 삭제 하였습니다.");
                            pathList.remove(parent.indexOfChild(selectedView)-1);//이 뷰가 parent의 몇번째에 있는 자식인지 알아온다
                            parent.removeView(selectedView); //getParent()하면 부모뷰에 접근을 한다.
                            buttonsBackgroundLayout.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            showToast(WritePostActivity.this,"파일을 삭제하는데 실패하였습니다.");
                        }
                    });

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
            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //firestore 초기화

            final DocumentReference documentReference = postInfo==null?firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());
            final Date date = postInfo ==null ? new Date() : postInfo.getCreatedAt();
            for (int i = 0; i < parent.getChildCount(); i++) {   //안에 자식 뷰만큼 반복을 할거다
                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i); //자식뷰먼저 저장해주고!!(parent(부모) 안에 있는 뷰에 하나하나 접근을 할거다)
                for(int ii=0;ii<linearLayout.getChildCount();ii++){
                    View view =linearLayout.getChildAt(ii);

                    if (view instanceof EditText) { //★view가 만약에 EditText가 있으면 이게 실행이 되야된다.
                        String text = ((EditText)view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);   //여기가 텍스트 넣는 부분
                            formatList.add("text");
                        }
                    } else if(!isStorageUrl(pathList.get(pathCount))){    //경로가 URL이 아닐때만 경로를 처리하도록 한다 (URL 처리 부분)
                        String path=pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);
                        if(isImageFile(path)){   //이미지면
                            formatList.add("image");
                        }else if(isVideoFile(path)){    //비디오면
                            formatList.add("video");
                        }else{                       //그 외는 전부 텍스트로 표시
                            formatList.add("text");
                        }
                        String[] pathArray =path.split("\\.");  // . 기준으로 string 배열로 반환을 해준다.그 중에 마지막 값이 확장자가 된다  (그러면 바로 아랫줄에 파일 확장자명에 맞게 잘 들어 갈 것이다.)
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + pathArray[pathArray.length-1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();    //API에 사용 법 있음.StorageMetadata
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            Log.e("로그", "uri : " + uri);  //사진 -> uri
                                            contentsList.set(index, uri.toString());
                                            if (successCount==0) {
                                                //완료
                                                PostInfo postInfo = new PostInfo(title, contentsList,formatList, user.getUid(),date);    //user.getUid()  = 로그인한 사용자
                                                storeUpload(documentReference, postInfo);
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
            if(successCount==0){
                storeUpload(documentReference,  new PostInfo(title, contentsList, formatList, user.getUid(), date));    //user.getUid()  = 로그인한 사용자
            }

        } else {
            showToast(WritePostActivity.this,"제목을 입력해주세요.");
            //Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_LONG).show();
        }
    }


    private void storeUpload(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        loaderLayout.setVisibility(View.GONE);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("postinfo",postInfo);
                        setResult(Activity.RESULT_OK,resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        loaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    private void postInit(){
        if(postInfo != null){
            titleEditText.setText(postInfo.getTitle());
            ArrayList<String> contentsList = postInfo.getContents();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) {  //1.URL인지를 검사 하는 방법 && 2.URL 경로가 맞는지 검사
                    pathList.add(contents);
                    ContentsItemView contentsItemView=new ContentsItemView(this);
                    parent.addView(contentsItemView);


                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView=(ImageView)v;  //선택이 되었을 때 전역으로 저장하는 곳

                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                    if(i<contentsList.size()-1){
                        String nextContents =contentsList.get(i+1);
                        if(!isStorageUrl(nextContents)){
                            contentsItemView.setText(nextContents);
                        }
                    }
                }else if(i==0){
                    contentsEditText.setText(contents);
                }
            }
        }
    }

    private void myStartActivity(Class c, int media,int requestCode) {
        Intent intent = new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_MEDIA, media); //값 전달
        startActivityForResult(intent, requestCode);  //사진 찍은 거 . 결과 받아야하니깐 startActivityForResult 사용함
    }

}
