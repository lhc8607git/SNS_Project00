package com.example.sns_project00.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sns_project00.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.sns_project00.Util.showToast;

public class SignUpActivity extends BasicActivity {
    //private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setToolbarTitle("회원가입"); //액션바 이름

        //auth 연결
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnsignup).setOnClickListener(onClickListener);
        findViewById(R.id.btnsignupGOlogin).setOnClickListener(onClickListener);
    }



    @Override
    public void onBackPressed() {      //★ 뒤로가기버튼 누를 시
        super.onBackPressed();
        moveTaskToBack(true);                                //★ 여기서부터 3줄은 아애 뒤로가기버튼 누르면 꺼지게 하는 방법이다.
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnsignup:
                    signUp();
                    break;
                case R.id.btnsignupGOlogin:
                    myStartActivity(LoginActivity.class);
                    break;
            }
        }
    };

    private void signUp() {
        String email = ((EditText) findViewById(R.id.edtsignemail)).getText().toString();
        String password = ((EditText) findViewById(R.id.edtsignpasswd)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.edtsignpasswdcheck)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) {
                final RelativeLayout loaderLayout=findViewById(R.id.loaderLayout);
                loaderLayout.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loaderLayout.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    showToast(SignUpActivity.this,"회원가입에 성공하셨습니다");
                                   // startToast("회원가입에 성공하셨습니다");
                                    myStartActivity(MainActivity.class);   //성공시 바로 이동
                                    //성공했을 때 로직 UI
                                } else {
                                    if(task.getException()!=null){
                                        showToast(SignUpActivity.this,task.getException().toString());
                                      //  startToast(task.getException().toString());
                                    }

                                    //실패 했을 때 UI 로직
                                }
                            }
                        });
            } else {
                showToast(SignUpActivity.this,"비밀번호가 일치하지 않습니다.");
               // Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            }

        } else {
            showToast(SignUpActivity.this,"이메일 또는 비밀번호를 입력해 주세요.");
          //  Toast.makeText(getApplicationContext(), "이메일 또는 비밀번호를 입력해 주세요.", Toast.LENGTH_LONG).show();
        }
    }

    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void myStartActivity(Class c){
        Intent intent=new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //뒤로가기 할때 깨끗하게
        startActivity(intent);
    }
}
