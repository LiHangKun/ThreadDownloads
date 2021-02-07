package com.example.threaddownloads;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
//可用的单线程下载  不咋地
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getCanonicalName();
    private ImageView imageview;
    private Button load;
    String path = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"; // 下载地址
    private int threadsum = 3;
    private RandomAccessFile raf;
    private File file;
    public static int threadCount=1;//进行下载的线程数量
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageview = (ImageView) findViewById(R.id.imageview);
        file = createFile(path);
        load = (Button) findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i=0;
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            String absolutePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator;
                            URL url=new URL(path);
                            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setConnectTimeout(8000);
                            conn.setReadTimeout(8000);
                            conn.connect();
                            if (conn.getResponseCode()==200) {
                                int length=conn.getContentLength();//返回文件大小
                                Log.i("aaa","文件大小"+length);
                                //占据文件空间
                                int size=length/threadCount;
                                for (int id = 0; id < threadCount; id++) {
                                    //1、确定每个线程的下载区间
                                    //2、开启对应子线程下载
                                    int startIndex=id*size;
                                    int endIndex=(id+1)*size-1;
                                    if (id==threadCount-1) {
                                        endIndex=length-1;
                                    }
                                    System.out.println("第"+id+"个线程的下载区间为"+startIndex+"--"+endIndex);
                                    new DownLoadThread(startIndex, endIndex, path, id).start();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();



               /* if (null == raf){
                    try {
                        raf = new RandomAccessFile(file, "rwd");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                somethreadload(path,raf,threadsum);*/
            }
        });
    }

    private File createFile(String urlload) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            String absolutePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator;
        /*    int i = urlload.lastIndexOf("/");
            String filepath = absolutePath + urlload.substring(i);
            File file = new File(absolutePath);
            if (!file.exists()){
                file.mkdir();
            }*/
            //目标文件
            File dstfile = new File(absolutePath+System.currentTimeMillis()+".mp4");
            if (!dstfile.exists()){
                try {
                    dstfile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                dstfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dstfile;
        }else {
            Log.e(TAG, "createFile: "+"没有sd卡" );
        }
        return null;
    }
    class DownLoadThread extends Thread{
        private int startIndex,endIndex,threadId;
        private String urlString;
        public DownLoadThread(int startIndex,int endIndex,String urlString,int threadId) {
            this.endIndex=endIndex;
            this.startIndex=startIndex;
            this.urlString=urlString;
            this.threadId=threadId;
        }
        @Override
        public void run() {
            try {
                String absolutePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator;
                URL url=new URL(urlString);
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);//设置头信息属性,拿到指定大小的输入流
                    InputStream is=conn.getInputStream();
                    File file =new File(absolutePath+System.currentTimeMillis()+".mp4");
                    RandomAccessFile mAccessFile=new RandomAccessFile(file, "rwd");//"rwd"可读，可写
                    mAccessFile.seek(startIndex);//表示从不同的位置写文件
                    byte[] bs=new byte[1024];
                    int len=0;
                    int current=startIndex;
                    while ((len=is.read(bs))!=-1) {
                        mAccessFile.write(bs,0,len);
                        current+=len;
                        System.out.println("第"+threadId+"个线程下载了"+current);
                    }
                    mAccessFile.close();
                    System.out.println("第"+threadId+"个线程下载完毕");
                    i++;
                    Log.i("aaa","i是几---"+i);
                    if(i==2){
                        boolean interrupted = DownLoadThread.currentThread().isInterrupted();
                        Log.i("aaa","执行完了没---"+interrupted);
                        saveFile2Album(file,true);
                        aa();
                }

                /*boolean interrupted = DownLoadThread.currentThread().isInterrupted();
                Log.i("aaa","执行完了没---"+interrupted);*/
               /* saveFile2Album(file,true);
                aa();*/

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    private void aa() {
        updateUI(this);
    }

    public void updateUI(final Context context) {
        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //此时已在主线程中，可以更新UI了
                Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void saveFile2Album(File file, boolean video) throws IOException {
        if (file == null){
            Log.i("aaa","空的");
            return;
        }
        Log.i("aaa","不空----"+file.getPath());
        ContentResolver contentResolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("title", file+"");
        values.put("_display_name", file+"");
        values.put("datetaken", System.currentTimeMillis());
        values.put("date_modified", System.currentTimeMillis());
        values.put("date_added", System.currentTimeMillis());
        values.put("_data", file+"");
        values.put("_size", file.length());
        Uri uri;
        if (video) {
            values.put("mime_type", "video/mp4");
            values.put("duration", "60000");
            uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            Log.i("aaa","values     ---"+values);
            Log.i("aaa","file     ---"+file);
            Log.i("aaa","走道了video里面了     ---"+uri);
        } else {
            values.put("mime_type", "image/jpeg");
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file+""))));
        ContentValues a = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, "file://"+file);
        values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
        Uri r = MainActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, a);        // 最后通知图库更新
        Log.i("aaa","fileName---"+file);
        MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));

    }
}

