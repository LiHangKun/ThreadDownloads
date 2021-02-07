package com.example.threaddownloads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModelDownload {
    /*
     * 获取http连接处理类HttpURLConnection
     */
    private HttpURLConnection getConnection(String strUrl)
    {
        URL url;
        HttpURLConnection urlcon = null;
        try {
            url = new URL(strUrl);
            urlcon = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlcon;
    }

    /*
     * 写文件到sd卡 demo
     * 前提需要设置模拟器sd卡容量，否则会引发EACCES异常
     * 先创建文件夹，在创建文件
     */
    public boolean down2sd(String strUrl, File file, downhandler handler) {
        HttpURLConnection urlcon = getConnection(strUrl);
        if (urlcon == null){
            return false;
        }

        FileOutputStream outputStream = null;
        try {
            int currentSize =0;
            int totalSize =0;
            InputStream inputStream = urlcon.getInputStream();
            if (200 != urlcon.getResponseCode()){
                return false;
            }
            totalSize = urlcon.getContentLength();
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024*10];
            while (true) {
                int iReadSize = inputStream.read(buffer);
                if (-1 == iReadSize){
                    break;
                }
                outputStream.write(buffer,0,iReadSize);
                //同步更新数据
                currentSize = currentSize+iReadSize;
                handler.setSize(currentSize,totalSize);
            }
            inputStream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
     * 内部回调接口类
     */
    public abstract static class downhandler
    {
        public abstract void setSize(int currentSize,int totalSize);
    }
}