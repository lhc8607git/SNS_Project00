package com.example.sns_project00.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.sns_project00.MemberInfo;
import com.example.sns_project00.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.sns_project00.Util.INTENT_PATH;
import static com.example.sns_project00.Util.showToast;

public class MemberInitActivity extends BasicActivity {
    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageView;
    private RelativeLayout loaderLayout;
    private String profilePath;         //경로도 전역변수로 바꿔줌.;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);
        setToolbarTitle("회원정보"); //액션바 이름

        loaderLayout=findViewById(R.id.loaderLayout);
        profileImageView= findViewById(R.id.profileimg);
        profileImageView.setOnClickListener(onClickListener);

        findViewById(R.id.btncheckINIT).setOnClickListener(onClickListener);
        findViewById(R.id.picture).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override       //일딴 ,,,,,,,,, 값을 확인하는곳?  받는 곳?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {      //MemberInitActivity.java에 myStartActivity메소드가  보낸 결과를 받는 곳
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0 :{                                                                  //myStartActivity메소드에서 requestCode를 0으로 보낸다고 해서
                if(resultCode == Activity.RESULT_OK){
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Log.e("로그","profilePath: "+ profilePath);               //지워도됨.....찍은 사진의 저장된 위치 알려고
//                    image리사이징(외부 라이브러리)를 사용하기 때문에 밑에 2줄을 사용하지 않기 때문에 주석 닮.
//                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);                 //저장된 사진의 위치가 String으로 되어 있어서 Bitmap으로 디코더파일을 해서 이미지로 바꿔준다!!!
//                    profileImageView.setImageBitmap(bmp);
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView); //image리사이징(외부 라이브러리)
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btncheckINIT:
                    storageUploader();
                    break;
                case R.id.profileimg:
                    CardView cardView=findViewById(R.id.buttonsCardView);
                    if(cardView.getVisibility() == View.VISIBLE){
                        cardView.setVisibility(View.GONE);
                    }else {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.picture:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.gallery:
                    myStartActivitygalleryerror(GalleryActivity.class);
                    break;
            }
        }
    };




    private void storageUploader() {
        final String name = ((EditText) findViewById(R.id.edtnameINIT)).getText().toString();
        final String phonenum = ((EditText) findViewById(R.id.edtphonenumINIT)).getText().toString();
        final String birthday = ((EditText) findViewById(R.id.edtbirthdayINIT)).getText().toString();
        final String address = ((EditText) findViewById(R.id.editaddressINIT)).getText().toString();

        if (name.length() > 0 && phonenum.length()>9 && birthday.length()>5 && address.length()>0) {
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();       //그래서 이렇게 수정함.
            final StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/profileImage.jpg");   //★★사용자의 Uid 가져와서 각각 넣음(구별)★★

            if(profilePath == null){
                MemberInfo memberInfo = new MemberInfo(name,phonenum,birthday,address);
                storeUploader(memberInfo);
            }else{
                try{
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.e("성공","성공 : "+downloadUri);

                                MemberInfo memberInfo = new MemberInfo(name,phonenum,birthday,address,downloadUri.toString());
                                storeUploader(memberInfo);
                            } else {

                                showToast(MemberInitActivity.this,"회원정보를 보내는데 실패하였습니다.");
                                //Toast.makeText(getApplicationContext(), "회원정보를 보내는데 실패하였습니다.", Toast.LENGTH_LONG).show();
                                Log.e("로그","실패");
                            }
                        }
                    });
                }catch (FileNotFoundException e){
                    Log.e("로그","에러 : "+e.toString());
                }
            }
        }else {
            showToast(MemberInitActivity.this,"회원정보를 입력해주세요.");
           // Toast.makeText(getApplicationContext(), "회원정보를 입력해주세요.", Toast.LENGTH_LONG).show();
        }
    }

    private void storeUploader(MemberInfo memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance(); //firestore 초기화
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {          //성공 했을때 - 토스트로 알려주고 싶어서서
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(MemberInitActivity.this,"회원정보 등록을 성공하였습니다.");
                       // Toast.makeText(getApplicationContext(), "회원정보 등록을 성공하였습니다.", Toast.LENGTH_LONG).show();
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {         //실패 했을때 - 토스트로 알려주고 싶어서서
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(MemberInitActivity.this,"회원정보 등록을 실패하였습니다.");
                        //Toast.makeText(getApplicationContext(), "회원정보 등록을 실패하였습니다.", Toast.LENGTH_LONG).show();
                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }


    private void myStartActivity(Class c){
        Intent intent=new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //뒤로가기 할때 깨끗하게
        startActivityForResult(intent,0);  //사진 찍은 거 . 결과 받아야하니깐 startActivityForResult 사용함
    }


    //아.. 이거 myStartActivity 이걸로 했었는데 에러 나서 이걸로함.. 나중에 해결되면 삭제.
    private void myStartActivitygalleryerror(Class c){
        Intent intent=new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("media","gallery");
        startActivityForResult(intent,0);  //사진 찍은 거 . 결과 받아야하니깐 startActivityForResult 사용함
    }
}
