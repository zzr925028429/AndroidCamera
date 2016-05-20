package com.wangyeming.androidcamerademo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wangyeming.simplecamera.TakePhotoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * 主页面
 */
@SuppressLint("SdCardPath")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_FOR_CAMERA = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private TextView vPath;
    private ImageView vPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button vCamera2 = (Button) findViewById(R.id.camera_route_camera2);
        Button btn1 = (Button) findViewById(R.id.ppp);
        Button btn2=(Button) findViewById(R.id.lx);
        vCamera2.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        vPath = (TextView) findViewById(R.id.camera_image_path);
        vPreview = (ImageView) findViewById(R.id.camera_image_preview);
        vCamera2.setEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camera_route_camera2) {
            TakePhotoUtils.takePhotoByCamera2(this, REQUEST_CODE_FOR_CAMERA);
        } else if (v.getId() == R.id.ppp) {

          Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else if(v.getId()==R.id.lx) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        }


    }

    @SuppressLint("SdCardPath")
    @Override//自定义相机
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_CAMERA) {
            if (resultCode == RESULT_OK) {
                String path = TakePhotoUtils.parsePhotoPath(data);
                long createTime = TakePhotoUtils.parseCreateTime(data);
                vPath.setText(path + " createTime " + createTime);
                Glide.with(this)
                        .load(path)
                        .fitCenter()
                        .thumbnail(0.1f)
                        .into(vPreview);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();

            }

        }
        //调用系统相机
        else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Log.i("TestFile", "SD card is not avaiable/writeable right now.");
                    return;
                }
                new DateFormat();

                String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                Toast.makeText(this, name, Toast.LENGTH_LONG).show();
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

                FileOutputStream b = null;
                File file = new File("/sdcard/Image/");
                file.mkdirs();// 创建文件夹
                String fileName = "/sdcard/Image/" + name;

                try {
                    b = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    vPreview.setImageBitmap(bitmap);// 将图片显示在ImageView里
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }

       else if(requestCode==CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE){
if(resultCode==RESULT_OK){
    String path=data.getData().toString();
    Log.i("zzr",path);
}
        }
    }
}
