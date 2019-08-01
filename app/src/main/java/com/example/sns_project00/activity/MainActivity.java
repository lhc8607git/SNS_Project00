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

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private MainAdapter mainAdapter;
    private  ArrayList<PostInfo> postList;
    private boolean updating;
    private boolean topScrolled; //스크롤이 시작했을 때부터 끝 날때까지 쳌크하는거

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarTitle(getResources().getString(R.string.app_name)); //액션바 이름 (strings.xml에 정의 된 앱이름으로 설정)



       //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //android 화면 (세로 모드로 고정)
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();//중복되는 곳이 있어서 --------- (그냥,참고,,, 사용자의 UID를 사용할려면 이 코드 사용 해야함)


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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {            // 스크롤 상태가 변했을 때 (그냥 스크롤 슉해도 인식을 받는 녀석)
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager =recyclerView.getLayoutManager();
                int firstVisibleItemPosition =((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();  //화면에 보이는 첫번째

                if(newState == 1 && firstVisibleItemPosition == 0){
                    topScrolled=true;
                }
                if (newState==0 && topScrolled){  //손가락을 땠을 때
                    postList.clear();
                    PostUpdate();

                    topScrolled=false;

                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){  //그냥 스크롤이 되는 네네 작동하는거
                super.onScrolled(recyclerView, dx, dy);

                //스코릴이 될때마다 화면에 보이는 아이템이 몇번째 인지 알 수 있는 방법
                RecyclerView.LayoutManager layoutManager =recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount =layoutManager.getItemCount();
                int firstVisibleItemPosition =((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();  //화면에 보이는 첫번째
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();  //화면에 보이는 마지막째

                if(totalItemCount -3 <= lastVisibleItemPosition && !updating){
                    PostUpdate();
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled=false;
                }

            }
        });


        PostUpdate(); //게시물 읽어오는고 업데이트 등등

 
    }

    @Override
    protected void onResume(){  //(액티비티가 재실행 되거나, 다시 왔을 때) 이것을 사용한다.
        super.onResume();
       // PostUpdate();  포스트 업데이트하는거거
    }

    @Override
    protected void onPause() { //액티비티가 멈춰질떄 나타내는거
        super.onPause();
        mainAdapter.playerStop();
    }

    @Override       //일딴 ,,,,,,,,, 값을 확인하는곳?  받는 곳?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if(data != null){
                    PostUpdate();
                }
                break;
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                /*
                case R.id.btnmainlogout:
                    FirebaseAuth.getInstance().signOut();   //★ Firebase Auth 로그아웃 하는 방법  (로그아웃, 로그아웃로그아웃, 로그아웃로그아웃, 로그아웃로그아웃, 로그아웃)
                    myStartActivity(SignUpActivity.class);
                    break;
                */
                case R.id.floatingActionButton:
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }
    };

    OnPostListener onPostListener=new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            postList.remove(postInfo);
            mainAdapter.notifyDataSetChanged();

            Log.e("로그 : ","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그 : ","수정 성공");
        }
    };



    private void PostUpdate(){
        if(firebaseUser!=null){
            updating =true;
            Date date = postList.size() ==0 ? new Date() : postList.get(postList.size() -1).getCreatedAt();
            CollectionReference collectionReference=firebaseFirestore.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt",date).limit(10).get()     //limit 10개씩 일거오는거
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(    //추가부분
                                            document.getData().get("title").toString(),       //여기는 읽는 부분----------------------------------
                                            (ArrayList<String>)document.getData().get("contents"),
                                            (ArrayList<String>)document.getData().get("formats"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId()));
                                    Log.e("로그","데이터 : "+document.getData().get("title").toString());
                                }
                                mainAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                            updating=false;
                        }
                    });
        }
    }


    private void myStartActivity(Class c){
        Intent intent=new Intent(this, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //뒤로가기 할때 깨끗하게
        startActivityForResult(intent,0);
    }

}
