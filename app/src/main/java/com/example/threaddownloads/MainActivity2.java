package com.example.threaddownloads;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
//可以用的okhttp 下载
public class MainActivity2 extends AppCompatActivity {
    private File thirdFile;
    private File dir;
    private File getDir(){
        if (dir!=null && dir.exists()){
            return dir;
        }

        dir = new File(this.getExternalCacheDir(), "download");
        if (!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView tx = findViewById(R.id.tx);
        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // url服务器地址，saveurl是下载路径，fileName表示的是文件名字
                DownloadUtil.get().download(MainActivity2.this, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",getExternalCacheDir()+"/download",System.currentTimeMillis()+".mp4",  new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity2.this, "下载成功", Toast.LENGTH_SHORT).show();
                                /*// 这里的弹框设置了进度条，下同
                                dialog.dismiss();

                                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    return;
                                }

                                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/download/"+fileName);
                                Log.w(TAG,"路径2："+file);
                                try {
                                    Log.w(TAG,"打开");
                                    OpenFileUtils.openFile(mContext, file);
                                } catch (Exception e) {
                                    Log.w(TAG,"无打开方式");
                                    e.printStackTrace();
                                }*/
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {
                        /*dialog.setProgress(progress);*/
                        Log.i("aaa","下载进度   "+progress);
                    }

                    @Override
                    public void onDownloadFailed() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity2.this, "下载失败", Toast.LENGTH_SHORT).show();
                                /*dialog.dismiss();*/
                            }
                        });
                    }
                });


            }
        });

    }
}