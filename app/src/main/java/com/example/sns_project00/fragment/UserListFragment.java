package com.example.sns_project00.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;
import com.example.sns_project00.UserInfo;
import com.example.sns_project00.activity.WritePostActivity;
import com.example.sns_project00.adapter.UserListAdapter;
import com.example.sns_project00.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserListFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore firebaseFirestore;
    private UserListAdapter userListAdapter;
    private ArrayList<UserInfo> userList;
    private boolean updating;
    private boolean topScrolled; //스크롤이 시작했을 때부터 끝 날때까지 쳌크하는거

    public UserListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_user_list, container, false);

        //사용자 정보 작성 안 했으면 보이고, 작성 했으면 안보이고 설정해줌.
        firebaseFirestore = FirebaseFirestore.getInstance(); //firestore 초기화

        userList = new ArrayList<>();
        //String[] myDataset={"강아지","고양이","드래곤","치킨"};   //임시사용
        userListAdapter = new UserListAdapter(getActivity(),userList);

        final RecyclerView recyclerView =view.findViewById(R.id.recyclerVeiwuerlist);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userListAdapter);
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
                    PostUpdate(true);  //firedatabase에서 데이터를 load를 한다.
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
                    PostUpdate(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled=false;
                }

            }
        });


        PostUpdate(false); //게시물 읽어오는고 업데이트 등등

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() { //액티비티가 멈춰질떄 나타내는거
        super.onPause();
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
            userList.remove(postInfo);
            userListAdapter.notifyDataSetChanged();

            Log.e("로그 : ","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그 : ","수정 성공");
        }
    };



    private void PostUpdate(final boolean clear){

        updating =true;
        //Date date = userList.size() ==0 || clear ? new Date() : userList.get(userList.size() -1).getCreatedAt();
        CollectionReference collectionReference=firebaseFirestore.collection("users");
        collectionReference.get()     //.limit(100)  -> 10개씩 일거오는거
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                userList.clear(); //데이터를 한번 지워주고
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {    //데이터를 넣어준다
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                userList.add(new UserInfo(    //추가부분
                                        document.getData().get("name").toString(),       //여기는 읽는 부분----------------------------------
                                        document.getData().get("phonenum").toString(),
                                        document.getData().get("birthday").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("photoUrl") == null ? null : document.getData().get("photoUrl").toString()));
                            }
                            userListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating=false;
                    }
                });
    }


    private void myStartActivity(Class c){
        Intent intent=new Intent(getActivity(), c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //뒤로가기 할때 깨끗하게
        startActivityForResult(intent,0);
    }
}
