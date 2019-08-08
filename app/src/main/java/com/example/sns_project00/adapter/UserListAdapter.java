package com.example.sns_project00.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sns_project00.R;
import com.example.sns_project00.UserInfo;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MainViewHolder> {
    private ArrayList<UserInfo> mDataset;
    private Activity activity;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public UserListAdapter(Activity activity, ArrayList<UserInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity=activity;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public UserListAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {     // --------이 부분  리스트에 Adapter에서 사용할 레이아웃을 설정해주는거 (이거 해봤잖어. 그냥 이거 쓰겠다라는거(레이아웃))
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //

            }
        });

        return mainViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView photoimageView=cardView.findViewById(R.id.photoimageView);
        TextView nameTextView =cardView.findViewById(R.id.nameTextView);
        TextView addressTextView =cardView.findViewById(R.id.addressTextView);

        UserInfo userInfo=mDataset.get(position);
        if(mDataset.get(position).getPhotoUrl() != null){ //이미지가 없으면 기본이미지로 나오게 설정
            Glide.with(activity).load(mDataset.get(position).getPhotoUrl()).centerCrop().override(500).into(photoimageView); //image리사이징(외부 라이브러리)
        }


        nameTextView.setText(userInfo.getName());
        addressTextView.setText(userInfo.getAddress());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}