package com.example.sns_project00.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.sns_project00.Util.isStorageUrl;

public class PostActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        PostInfo postInfo=(PostInfo)getIntent().getSerializableExtra("postInfo");
        TextView titleTextView =findViewById(R.id.titleTextView);
        titleTextView.setText(postInfo.getTitle());

        TextView createdAtTextView =findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt()));
//(바로 밑코드) image리사이징(외부 라이브러리)로 바꿈  <지워두됨>
//        Bitmap bmp = BitmapFactory.decodeFile(postInfo);         //저장된 사진의 위치가 ImageView로 되어 있어서 ★Bitmap으로 디코더파일★을 해서 이미지로 바꿔준다!!!
//        imageView.setImageBitmap(bmp);

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //여기다가 컨텐츠를 넣어주면된다
        ArrayList<String> contentsList = postInfo.getContents();

        if(contentsLayout.getTag()==null || !contentsLayout.getTag().equals(contentsList)){
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) {  //1.URL인지를 검사 하는 방법 && 2.URL 경로가 맞는지 검사
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true); //사진 비율이 맞춰진다
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView); //image리사이징(외부 라이브러리)
                } else {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    textView.setTextColor(Color.rgb(0,0,0));
                    contentsLayout.addView(textView);
                }
            }
        }
    }
}
