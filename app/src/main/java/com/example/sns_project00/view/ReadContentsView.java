package com.example.sns_project00.view;

import android.content.Context;
import android.net.Uri;
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
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReadContentsView  extends LinearLayout {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<SimpleExoPlayer> playerArrayList=new ArrayList<>();
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

    private void initView(){  //초기화
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
                final PlayerView playerView=(PlayerView) layoutInflater.inflate(R.layout.view_contents_player,this,false);  //화면 가져오는거???  (view_contents_image.xml을 가져오는 거)

                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                        Util.getUserAgent(context, getResources().getString(R.string.app_name)));  //이름 넣어주면 됨
                MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(contents));   //Uri로 바꿔줘야한다....

                SimpleExoPlayer player= ExoPlayerFactory.newSimpleInstance(context); //★player가 필요할 때마다 생성하게 해줘야 한다..(안해주면... 동영상 한 개만 실행됨..그래서 각각각..)

                player.prepare(videoSource);  //영상처리해주는 곳

                player.addVideoListener(new VideoListener() {
                    @Override
                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        playerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
                    }
                });

                playerArrayList.add(player);// 다만 들어준 player를 playerArrayList에다가 넣는다.

                playerView.setPlayer(player);
                contentsLayout.addView(playerView);   //레이아웃에다가 추가
            }else {
                TextView textView =(TextView)layoutInflater.inflate(R.layout.view_contents_text,this,false);  //화면 가져오는거???  (view_contents_text.xml을 가져오는 거)
//                textView.setLayoutParams(layoutParams);    이미 설정 됨
                textView.setText(contents);
//                textView.setTextColor(Color.rgb(0,0,0));    이미 설정 됨
                contentsLayout.addView(textView);
            }

        }
    }

    public ArrayList<SimpleExoPlayer> getPlayerArrayList(){  //언제든지 playerlist를 가져와서 정지를 시켜줄려고. 일딴 만듬
        return playerArrayList;
    }

}
