package com.example.sns_project00.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.GalleryViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        mDataset = myDataset;
        this.activity=activity;
    }

    @NonNull
    @Override
    public MainAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {     // --------이 부분  리스트에 Adapter에서 사용할 레이아웃을 설정해주는거 (이거 해봤잖어. 그냥 이거 쓰겠다라는거(레이아웃))
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return galleryViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView textView =cardView.findViewById(R.id.textView);
        textView.setText(mDataset.get(position).getTitle());
//(바로 밑코드) image리사이징(외부 라이브러리)로 바꿈  <지워두됨>
//        Bitmap bmp = BitmapFactory.decodeFile(mDataset.get(position));         //저장된 사진의 위치가 ImageView로 되어 있어서 ★Bitmap으로 디코더파일★을 해서 이미지로 바꿔준다!!!
//        imageView.setImageBitmap(bmp);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}