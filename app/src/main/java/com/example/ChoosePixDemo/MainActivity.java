package com.example.ChoosePixDemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.io.File;

public class MainActivity extends Activity {

    private ImageView mIvGroupchat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvGroupchat= (ImageView) findViewById(R.id.picture);
        layout_all = (LinearLayout)findViewById(R.id.activity_main);
mIvGroupchat.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        showMyDialog();
    }
});


    }



    //可以选择相册图片，可以调用系统相机，在使用图片的时候可以裁剪
    private RelativeLayout layout_choose;
    private RelativeLayout layout_photo;
    private RelativeLayout layout_close;

    private LinearLayout layout_all;
    protected int mScreenWidth;

    /**
     * 定义三种状态
     */
    private static final int REQUESTCODE_PIC = 1;//相册
    private static final int REQUESTCODE_CAM = 2;//相机
    private static final int REQUESTCODE_CUT = 3;//图片裁剪

    private Bitmap mBitmap;
    private File mFile;

    PopupWindow avatorPop;

    public void showMyDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pop_show,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_close = (RelativeLayout) view.findViewById(R.id.layout_close);



        layout_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openCamera();
                //隐藏对话框
                avatorPop.dismiss();
            }
        });

        layout_choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                openPic();
                //隐藏对话框
                avatorPop.dismiss();

            }
        });

        layout_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //隐藏对话框
                avatorPop.dismiss();
            }
        });


        DisplayMetrics metric = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        avatorPop = new PopupWindow(view, mScreenWidth, 200);
        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        //设置宽度
        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //设置高度
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //可以点击
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 打开相册
     */
    private void openPic() {
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, REQUESTCODE_PIC);
    }

    /**
     * 调用相机
     */
    private void openCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()) {
                file.mkdirs();
            }
            mFile = new File(file, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, REQUESTCODE_CAM);
        } else {


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_CAM:
                    startPhotoZoom(Uri.fromFile(mFile));
                    break;
                case REQUESTCODE_PIC:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    startPhotoZoom(data.getData());
                    break;
                case REQUESTCODE_CUT:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            //这里也可以做文件上传
            mBitmap = bundle.getParcelable("data");
            mIvGroupchat.setImageBitmap(mBitmap);
        }
    }

    /**
     * 打开系统图片裁剪功能
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true); //黑边
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUT);

    }


}