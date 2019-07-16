package com.example.sns_project00.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sns_project00.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends BasicActivity {
    //private static final String TAG = "PasswordResetActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        //auth 연결
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnpasswdresetSEND).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnpasswdresetSEND:
                    resetsend();
                    break;
            }
        }
    };

    private void resetsend() {   //찾을려고 하는 비밀번호의 이메일에다가 메시지를 보내는 메소드
        String email = ((EditText) findViewById(R.id.edtpasswdresetemail)).getText().toString();
        // String emailAddress = "user@example.com";   //임시로 이메일 만들고 써 볼려고(연습할려고)

        if (email.length() > 0) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startToast("이메일을 보냈습니다.");
                            }
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "이메일을 입력해 주세요.", Toast.LENGTH_LONG).show();
        }
    }

    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }


}
