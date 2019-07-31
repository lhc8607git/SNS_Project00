package com.example.sns_project00.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.sns_project00.PostInfo;
import com.example.sns_project00.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReadContentsView  extends LinearLayout {
    private Context context;
    private LayoutInflater layoutInflater;
    private int moreIndex=-1;


    public ReadContentsView(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public ReadContentsView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context=context;
        initView();
    }

    private void initView(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        layoutInflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_post,this,true);  //화면 가져오는거???  (view_post.xml을 가져오는 거)
    }

    public void setMoreIndex(int moreIndex){
        this.moreIndex=moreIndex;
    }

    public void setPostInfo(PostInfo postInfo){
        TextView createdAtTextView =findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt()));
//(바로 밑코드) image리사이징(외부 라이브러리)로 바꿈  <지워두됨>
//        Bitmap bmp = BitmapFactory.decodeFile(postInfo);         //저장된 사진의 위치가 ImageView로 되어 있어서 ★Bitmap으로 디코더파일★을 해서 이미지로 바꿔준다!!!
//        imageView.setImageBitmap(bmp);

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //여기다가 컨텐츠를 넣어주면된다
        ArrayList<String> contentsList = postInfo.getContents();
        ArrayList<String> formatsList = postInfo.getFormats();


        for (int i = 0; i < contentsList.size(); i++) {
            if(i==moreIndex){  //리스트에 추가할때 : 내용(작성하고),사진넣고,내용(또작성하고)   이렇게 하면 3개 잖아!!.  보여주는게 3개니깐. 이것을 "더보기.."로 대체한다는 코드이다.
                TextView textView = new TextView(context);
                textView.setLayoutParams(layoutParams);
                textView.setText("더보기..");
                contentsLayout.addView(textView);
                break;

            }

            String contents = contentsList.get(i);
            String formats = formatsList.get(i);

            if(formats.equals("image")){
                ImageView imageView = (ImageView)layoutInflater.inflate(R.layout.view_contents_image,this,false);  //화면 가져오는거???  (view_contents_image.xml을 가져오는 거)
//밑에 3줄 이미 다 설정 되어 있어서 주석닮.   (view_contents_image에 설정 되어 있음)
//                imageView.setLayoutParams(layoutParams);
//                imageView.setAdjustViewBounds(true); //사진 비율이 맞춰진다
//                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                contentsLayout.addView(imageView);
                Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView); //image리사이징(외부 라이브러리)
            }else if(formats.equals("video")){

            }else {
                TextView textView =(TextView)layoutInflater.inflate(R.layout.view_contents_text,this,false);  //화면 가져오는거???  (view_contents_text.xml을 가져오는 거)
//                textView.setLayoutParams(layoutParams);    이미 설정 됨
                textView.setText(contents);
//                textView.setTextColor(Color.rgb(0,0,0));    이미 설정 됨
                contentsLayout.addView(textView);
            }

        }
    }

}
