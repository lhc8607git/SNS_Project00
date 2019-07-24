package com.example.sns_project00.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project00.R;
import com.example.sns_project00.adapter.GalleryAdapter;

import java.util.ArrayList;

import static com.example.sns_project00.Util.showToast;

public class GalleryActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        //일딴, 먼저 필요한 권한부터 요청    --권한1-1(요청)
        if (ContextCompat.checkSelfPermission(GalleryActivity.this,   //권한이 없을때
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                showToast(GalleryActivity.this,"권한을 허용해 주세요.");
                //Toast.makeText(getApplicationContext(),"권한을 허용해 주세요.",Toast.LENGTH_LONG).show();
            }
        }else{                                                                  //권한이 있을때
            recyclerInit();
        }


    }

    //권한 요청 응답 처리    --권한1-2(응답)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recyclerInit();
                } else {
                    finish();
                    showToast(GalleryActivity.this,"권한을 허용해 주세요.");
                    //Toast.makeText(getApplicationContext(),"권한을 허용해 주세요.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void recyclerInit(){
        final int numberOfColumns=3;

        RecyclerView recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        //String[] myDataset={"강아지","고양이","드래곤","치킨"};   //임시사용
        RecyclerView.Adapter mAdapter = new GalleryAdapter(this,getImagesPath(this));     //myDataset이게 GalleryAdapter.java에 가보면 ArrayList<String>로 대어 있음.
        recyclerView.setAdapter(mAdapter);
    }

    public ArrayList<String> getImagesPath(Activity activity) {         //이젠 데이터가...그 나의 경로에 있는  이미지 리스트가 나와야겠죠!!
        Uri uri;                                                                 //이걸 (String[] myDataset={"강아지","고양이","드래곤","치킨"};),,,, 이 코드들로 사용 할꺼다.(임시)
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection; //핸드폰에 있는 비디오,앨범 넣을 배열

        Intent intent=getIntent();
        if(intent.getStringExtra("media").equals("video")){
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;    //비디오버튼누르면  비디오 갤러리화면 보이고
            projection = new String[]{ MediaStore.MediaColumns.DATA,MediaStore.Video.Media.BUCKET_DISPLAY_NAME };
        }else {                                                                    //아니면(이미지버튼누르면) 사진 갤러리화면 보인다.
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{ MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        }

        cursor = activity.getContentResolver().query(uri, projection,null,null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }
}
