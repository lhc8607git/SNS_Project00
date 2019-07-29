/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sns_project00.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;

import com.example.sns_project00.R;
import com.example.sns_project00.fragment.Camera2BasicFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.example.sns_project00.Util.INTENT_PATH;

public class CameraActivity extends BasicActivity {
    private Camera2BasicFragment camera2BasicFragment;         //카메라Flagment를 전역으로 해주고

//-----Camera2BasicFragment.java에서 가져온 메소드   (시작)-----------------------------------------------------------------------------------------------------------
    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {  //사진기에서 캡쳐누르면 발생하는 곳
            Image mImage = reader.acquireNextImage();
            File mFile = new File(getExternalFilesDir(null), "profileImage.jpg");

            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 값을 전해주고 꺼버림            (MemberInitActivity.java로 결과를 보내줌.)
            Intent intent = new Intent();
            intent.putExtra(INTENT_PATH,mFile.toString());   //결과보내는게 File타입이 없어서 일딴 문자로 보낸거다.
            setResult(Activity.RESULT_OK,intent);

            camera2BasicFragment.closeCamera(); //하... 사용했으면...카메라도 종료해줘야한다!!!★★★★★★★★★★
            finish();
        }
    };
//-------Camera2BasicFragment.java에서 가져온 메소드   (끝)---------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            camera2BasicFragment = new Camera2BasicFragment();     //사용
            camera2BasicFragment.setOnImageAvailableListener(mOnImageAvailableListener);  //사용
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2BasicFragment)
                    .commit();
        }
    }

}
