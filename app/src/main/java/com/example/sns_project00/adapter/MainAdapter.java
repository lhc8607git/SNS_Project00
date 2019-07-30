package com.example.sns_project00.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project00.FirebaseHelper;
import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;
import com.example.sns_project00.activity.PostActivity;
import com.example.sns_project00.activity.WritePostActivity;
import com.example.sns_project00.listener.OnPostListener;
import com.example.sns_project00.view.ReadContentsView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private final int MORE_INDEX=2;  //나중에 더보기 더 늘리고 싶으면 여기서 바꿔주면 됨-----------------------------------

    static class MainViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity=activity;

        firebaseHelper = new FirebaseHelper(activity);

    }

    public void setOnPostListener(OnPostListener onPostListener){
        firebaseHelper.setOnPostListener(onPostListener);
    }


    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {     // --------이 부분  리스트에 Adapter에서 사용할 레이아웃을 설정해주는거 (이거 해봤잖어. 그냥 이거 쓰겠다라는거(레이아웃))
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, PostActivity.class);
                intent.putExtra("postInfo",mDataset.get(mainViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
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

        PostInfo postInfo=mDataset.get(position);
        titleTextView.setText(postInfo.getTitle());

        ReadContentsView readContentsView=cardView.findViewById(R.id.readContentsView);
//       image리사이징(외부 라이브러리)로 바꿈  <지워두됨>
//        Bitmap bmp = BitmapFactory.decodeFile(mDataset.get(position));         //저장된 사진의 위치가 ImageView로 되어 있어서 ★Bitmap으로 디코더파일★을 해서 이미지로 바꿔준다!!!
//        imageView.setImageBitmap(bmp);
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);


        if(contentsLayout.getTag()==null || !contentsLayout.getTag().equals(postInfo)){
            contentsLayout.setTag(postInfo);
            contentsLayout.removeAllViews();

            readContentsView.setMoreIndex(MORE_INDEX);
            readContentsView.setPostInfo(postInfo);
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
                        myStartActivity(WritePostActivity.class,mDataset.get(position));
                        return true;
                    case R.id.delete:
                        firebaseHelper.storageDelete(mDataset.get(position));
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



    private void myStartActivity(Class c,PostInfo postInfo){
        Intent intent=new Intent(activity, c);  //클래스로 받는 걸로 바꿈.  <- Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("postInfo",postInfo);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //뒤로가기 할때 깨끗하게
        activity.startActivity(intent);
    }

}