package com.example.sns_project00.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.sns_project00.FirebaseHelper;
import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;
import com.example.sns_project00.listener.OnPostListener;
import com.example.sns_project00.view.ReadContentsView;

public class PostActivity extends BasicActivity {
    private PostInfo postInfo;
    private FirebaseHelper firebaseHelper;
    private  ReadContentsView readContentsView;
    private LinearLayout contentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postInfo=(PostInfo)getIntent().getSerializableExtra("postInfo");

        contentsLayout = findViewById(R.id.contentsLayout);
        readContentsView =findViewById(R.id.readContentsView);

        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.setOnPostListener(onPostListener);

        uiUpdate();
    }


    @Override       //일딴 ,,,,,,,,, 값을 확인하는곳?  받는 곳?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    postInfo =(PostInfo)data.getSerializableExtra("postinfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {       //땡땡땡 메뉴 버튼 생성하는거
        getMenuInflater().inflate(R.menu.post,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                firebaseHelper.storageDelete(postInfo);
                return true;
            case R.id.modify:
                myStartActivity(WritePostActivity.class,postInfo);
                return true;
             default:
                 return super.onOptionsItemSelected(item);
        }
    }

    OnPostListener onPostListener=new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            Log.e("로그","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그","수정 성공");
        }
    };

    private void uiUpdate(){
        setToolbarTitle(postInfo.getTitle()); //액션바 이름
        readContentsView.setPostInfo(postInfo);
    }

    private void myStartActivity(Class c,PostInfo postInfo){
        Intent intent=new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("postInfo",postInfo);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //뒤로가기 할때 깨끗하게
        startActivityForResult(intent,0);
    }
}
