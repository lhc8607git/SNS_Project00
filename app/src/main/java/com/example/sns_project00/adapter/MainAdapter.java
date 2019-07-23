package com.example.sns_project00.adapter;

import android.app.Activity;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cardView;
        MainViewHolder(Activity activity,CardView v, PostInfo postInfo) {
            super(v);
            cardView = v;

            LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //여기다가 컨텐츠를 넣어주면된다
            ArrayList<String> contentsList = postInfo.getContents();


            if (contentsLayout.getChildCount() == 0) {
                for (int i = 0; i < contentsList.size(); i++) {
                    String contents = contentsList.get(i);
                    if (Patterns.WEB_URL.matcher(contents).matches()) { //URL인지를 검사 하는 방법
                        ImageView imageView = new ImageView(activity);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setAdjustViewBounds(true); //사진 비율이 맞춰진다
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        contentsLayout.addView(imageView);
                    } else {
                        TextView textView = new TextView(activity);
                        textView.setLayoutParams(layoutParams);
                        contentsLayout.addView(textView);
                    }
                }
            }
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        mDataset = myDataset;
        this.activity=activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {     // --------이 부분  리스트에 Adapter에서 사용할 레이아웃을 설정해주는거 (이거 해봤잖어. 그냥 이거 쓰겠다라는거(레이아웃))
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(activity,cardView,mDataset.get(viewType));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v,mainViewHolder.getAdapterPosition());
            }
        });

        return mainViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {

        CardView cardView = holder.cardView;
        TextView titleTextView =cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());

        TextView createdAtTextView =cardView.findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));
//(바로 밑코드) image리사이징(외부 라이브러리)로 바꿈  <지워두됨>
//        Bitmap bmp = BitmapFactory.decodeFile(mDataset.get(position));         //저장된 사진의 위치가 ImageView로 되어 있어서 ★Bitmap으로 디코더파일★을 해서 이미지로 바꿔준다!!!
//        imageView.setImageBitmap(bmp);

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ArrayList<String> contentsList = mDataset.get(position).getContents();


        for(int i=0; i<contentsList.size();i++){
            String contents =contentsList.get(i);
            if(Patterns.WEB_URL.matcher(contents).matches()){ //URL인지를 검사 하는 방법
                Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into((ImageView)contentsLayout.getChildAt(i)); //image리사이징(외부 라이브러리)
            }else {
                ((TextView)contentsLayout.getChildAt(i)).setText(contents);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.modify:

                        return true;
                    case R.id.delete:

                        firebaseFirestore.collection("posts").document(mDataset.get(position).getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(activity,"게시글을 삭제하였습니다.",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity,"게시글을 삭제하지 못하였습니다.",Toast.LENGTH_LONG).show();
                                    }
                                });

                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

}