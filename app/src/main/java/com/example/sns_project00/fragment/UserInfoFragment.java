package com.example.sns_project00.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.sns_project00.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInfoFragment extends Fragment {
    private static final String TAG = "UserInfoFragment";


    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_user_info, container, false);
        final ImageView profileImageView= view.findViewById(R.id.profileImageView);
        final TextView nameTextView =view.findViewById(R.id.nameTextView);
        final TextView phoneNumberTextview =view.findViewById(R.id.phoneNumberTextview);
        final TextView birthDayTextView =view.findViewById(R.id.birthDayTextView);
        final TextView addressTextView =view.findViewById(R.id.addressTextView);

        //사용자 정보 가져오는 부분////////////////////////////////////////////////////////////////////////////
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {     //여기가 데이터가 성공적으로 오는부분
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if(document.getData().get("photoUrl") != null){ //이미지가 없으면 기본이미지로 나오게 설정
                                Glide.with(getActivity()).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileImageView); //image리사이징(외부 라이브러리)
                            }
                            nameTextView.setText(document.getData().get("name").toString());
                            phoneNumberTextview.setText(document.getData().get("phonenum").toString());
                            birthDayTextView.setText(document.getData().get("birthday").toString());
                            addressTextView.setText(document.getData().get("address").toString());

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
      ///////////////////////////////////////////////////////////////////////////////////////////////////////
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


}
