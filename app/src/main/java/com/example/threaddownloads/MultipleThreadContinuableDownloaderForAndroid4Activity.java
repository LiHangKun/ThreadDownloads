package com.example.threaddownloads;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.threaddownloads.service.downloader.DownloadProgressListener;
import com.example.threaddownloads.service.downloader.FileDownloader;

//多线程下载不可用
public class MultipleThreadContinuableDownloaderForAndroid4Activity extends Activity {
	public int i=0;
	private static final int PROCESSING = 1;// 正在下载实时数据传输Message标志
	private static final int FAILURE = -1;// 下载失败时的Message标志
	private EditText pathText;// 下载输入文本框
	private TextView resultView;// 实现进度显示百分比文本框
	private Button downloadButton;// 下载按钮，可以触发下载事件
	private Button stopbutton;// 停止按钮，可以停止下载
	private ProgressBar progressBar;// 下载进度条，实时图形化的显示进度信息
	// handler对象的作用是用于往创建Handler对象所在的线程所绑定的消息队列发送信息并处理信息
	private Handler handler = new UIHandler();

	private final class UIHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case PROCESSING:// 下载时
					int size = msg.getData().getInt("size");// 从消息中获取已经下载的数据长度

					progressBar.setProgress(size);// 设置进度条的进度
					float num = (float) progressBar.getProgress()
							/ (float) progressBar.getMax();// 计算已经下载的百分比，此处需要转换为浮点数计算
					int result = (int) (num * 100);// 把获取的浮点数计算结构专访为整数
					resultView.setText(result + "%");// 把下载的百分比显示在界面显示控件上
					if (progressBar.getProgress() == progressBar.getMax()) {
						Toast.makeText(getApplicationContext(), R.string.success,
								Toast.LENGTH_LONG).show();
						// 使用Toast技术，提示用户下载完成
						try {
							saveFile2Album(Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),true);
							/*updateUI(AppApplication.getContext());*/
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case FAILURE:// 下载失败
					Toast.makeText(getApplicationContext(), R.string.error,
							Toast.LENGTH_LONG).show();// 提示用户下载失败

			}
		}

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
		ContentResolver contentResolver = AppApplication.getContext().getContentResolver();
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

			Log.i("aaa","走道了video里面了     ---"+uri);
		} else {
			values.put("mime_type", "image/jpeg");
			uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		}
		AppApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

		AppApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));

		AppApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file+""))));
		ContentValues a = new ContentValues();
		values.put(MediaStore.Images.Media.DATA, "file://"+file);
		values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
		Uri r = AppApplication.getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, a);        // 最后通知图库更新
		Log.i("aaa","fileName---"+file);
		AppApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 应用程序启动时会首先调用而且在应用程序整个生命周期中只会调用一次，适合于初始化工作
		super.onCreate(savedInstanceState);// 使用父类的onCreate用做屏幕主界面的底层和基本绘制工作
		setContentView(R.layout.main);// 根据XML界面文件设置我们的主界面
		pathText = (EditText) this.findViewById(R.id.path);// 获取下载URL的文本输入对象
		resultView = (TextView) this.findViewById(R.id.resultView);// 获取显示下载百分比文本控制对象
		downloadButton = (Button) this.findViewById(R.id.downloadbutton);// 获取下载按钮对象
		stopbutton = (Button) this.findViewById(R.id.stopbutton);// 获取停止下载按钮对象
		progressBar = (ProgressBar) this.findViewById(R.id.progressBar);// 获取进度条对象
		ButtonClickListener listener = new ButtonClickListener();// 声明并定义按钮监听对象
		downloadButton.setOnClickListener(listener);
		stopbutton.setOnClickListener(listener);
	}

	/**
	 * 按钮监听器实现类
	 *
	 * @zhangxiaobo
	 */

	private final class ButtonClickListener implements View.OnClickListener {
		@SuppressLint({"ShowToast", "WrongConstant"})
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 该方法在注册了该按钮监听器的对象被单击时会自动调用，用力响应单击事件
			switch (v.getId()) {
				case R.id.downloadbutton:// 获取点击对象的id
					String path = pathText.getText().toString();// 获取下载路径
					Toast.makeText(getApplicationContext(), path, 1).show();
					if (Environment.getExternalStorageState().endsWith(
							Environment.MEDIA_MOUNTED)) {
						// 获取SDCard是否存在，当SDCard存在时
						Environment.getExternalStorageDirectory();// 获取SDCard根目录文件、
						File saveDir = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
						getExternalFilesDir(Environment.DIRECTORY_MOVIES);
						download(path, saveDir);
						Toast.makeText(getApplicationContext(), saveDir.toString(), 1).show();
					} else {
						// 当SDCard不存在时
						Toast.makeText(getApplicationContext(),
								R.string.sdcarderror, Toast.LENGTH_LONG).show();// 提示用户SDCard不存在
					}
					downloadButton.setEnabled(false);
					stopbutton.setEnabled(true);
					break;
				case R.id.stopbutton:
					exit();// 停止下载
					downloadButton.setEnabled(true);
					stopbutton.setEnabled(false);
					break;
			}
		}

	}

	// 由于用户的输入事件（点击button，触摸屏幕...）是由主线程负责处理的
	// 如果主线程处于工作状态
	// 此时用户产生的输入时间如果没能在5秒内得到处理，系统就会报应用无响应的错误
	// 所以在主线程里不能执行一件比较耗时的工作，否则会因主线程阻塞而无法处理用户的输入事件
	// 导致“应用无响应”错误的出现，耗时的工作应该在子线程里执行
	private DownloadTask task;// 声明下载执行者

	/**
	 * 退出下载
	 */
	public void exit() {
		if (task != null)
			task.exit();
	}

	/**
	 * 下载资源，声明下载执行者并开辟线程开始下载
	 *
	 * @param path
	 *            下载的路径
	 * @param saveDir
	 *            保存文件
	 */
	private void download(String path, File saveDir) {
		task = new DownloadTask(path, saveDir);// 实例化下载业务
		new Thread(task).start();
	}











	/**
	 * UI控制画面的重绘（更新）是由主线程负责处理的，如果在子线程中更新UI控件值，更新后值不会重绘到屏幕上
	 * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
	 */
	private final class DownloadTask implements Runnable {
		private String path;// 下载路径
		private File saveDir;// 下载到保存到的文件
		private FileDownloader loader;// 文件下载器（下载线程的容器）

		/**
		 * 构造方法，实现变量的初始化
		 *
		 * @param path
		 *            下载路径
		 * @param saveDir
		 *            下载要保存到的文件
		 */
		public DownloadTask(String path, File saveDir) {
			this.path = path;
			this.saveDir = saveDir;
		}

		/**
		 * 退出下载
		 */
		public void exit() {
			if (loader != null)
				loader.exit();// 如果下载器存在的话则退出下载
		}

		DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
			/**
			 * 下载的文件长度会不断地被传入该回调方法
			 */
			public void onDownloadSize(int size) {
				Log.i("aaaa","已经下载长度---"+size);
				Message msg = new Message();
				msg.what = PROCESSING;
				msg.getData().putInt("size", size);
				handler.sendMessage(msg);

			}
		};
		public void run() {
			// TODO Auto-generated method stub
			try {
				loader = new FileDownloader(getApplicationContext(), path, saveDir, 15);
				progressBar.setMax(loader.getFileSize());// 设置进度条的最大刻度
				loader.download(downloadProgressListener);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				handler.sendMessage(handler.obtainMessage(FAILURE));

			}
		}

	}
	public class DownloadThread extends Thread {
		private static final String TAG = "DownloadThread";	//定义TAG，方便日子的打印输出
		private File saveFile;	//下载的数据保存到的文件
		private URL downUrl;	//下载的URL
		private int block;	//每条线程下载的大小
		private int threadId = -1;	//初始化线程id设置
		private int downloadedLength;	//该线程已经下载的数据长度
		private boolean finished = false;	//该线程是否完成下载的标志
		private FileDownloader downloader;	//文件下载器

		public DownloadThread(FileDownloader downloader, URL downUrl, File saveFile, int block, int downloadedLength, int threadId) {
			this.downUrl = downUrl;
			this.saveFile = saveFile;
			this.block = block;
			this.downloader = downloader;
			this.threadId = threadId;
			this.downloadedLength = downloadedLength;
		}

		@Override
		public void run() {
			if(downloadedLength < block){//未下载完成
				try {
					HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();	//开启HttpURLConnection连接
					http.setConnectTimeout(5 * 1000);	//设置连接超时时间为5秒钟
					http.setRequestMethod("GET");	//设置请求的方法为GET
					http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");	//设置客户端可以接受的返回数据类型
					http.setRequestProperty("Accept-Language", "zh-CN");	//设置客户端使用的语言问中文
					http.setRequestProperty("Referer", downUrl.toString()); 	//设置请求的来源，便于对访问来源进行统计
					http.setRequestProperty("Charset", "UTF-8");	//设置通信编码为UTF-8
					int startPos = block * (threadId - 1) + downloadedLength;//开始位置
					int endPos = block * threadId -1;//结束位置
					http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);//设置获取实体数据的范围,如果超过了实体数据的大小会自动返回实际的数据大小
					http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");	//客户端用户代理
					http.setRequestProperty("Connection", "Keep-Alive");	//使用长连接

					InputStream inStream = http.getInputStream();	//获取远程连接的输入流
					byte[] buffer = new byte[1024];	//设置本地数据缓存的大小为1M
					int offset = 0;	//设置每次读取的数据量
					print("Thread " + this.threadId + " starts to download from position "+ startPos);	//打印该线程开始下载的位置
					RandomAccessFile threadFile = new RandomAccessFile(this.saveFile, "rwd");	//If the file does not already exist then an attempt will be made to create it and it require that every update to the file's content be written synchronously to the underlying storage device.
					threadFile.seek(startPos);	//文件指针指向开始下载的位置
					while (!downloader.getExited() && (offset = inStream.read(buffer, 0, 1024)) != -1) {	//但用户没有要求停止下载，同时没有到达请求数据的末尾时候会一直循环读取数据
						threadFile.write(buffer, 0, offset);	//直接把数据写到文件中
						downloadedLength += offset;	//把新下载的已经写到文件中的数据加入到下载长度中
						downloader.update(this.threadId, downloadedLength);	//把该线程已经下载的数据长度更新到数据库和内存哈希表中
						downloader.append(offset);	//把新下载的数据长度加入到已经下载的数据总长度中
					}//该线程下载数据完毕或者下载被用户停止

					threadFile.close();	//Closes this random access file stream and releases any system resources associated with the stream.
					inStream.close();	//Concrete implementations of this class should free any resources during close
					if(downloader.getExited())
					{
						print("Thread " + this.threadId + " has been paused");
					}
					else
					{
						print("Thread " + this.threadId + " download finish");
					}

					this.finished = true;	//设置完成标志为true，无论是下载完成还是用户主动中断下载
					Log.i("aaa","下载完成");


				} catch (Exception e) {	//出现异常
					this.downloadedLength = -1;	//设置该线程已经下载的长度为-1
					print("Thread "+ this.threadId+ ":"+ e);	//打印出异常信息
				}
			}
		}
		/**
		 * 打印信息
		 * @param msg	信息
		 */
		private  void print(String msg){
			Log.i(TAG, msg);	//使用Logcat的Information方式打印信息
		}

		/**
		 * 下载是否完成
		 * @return
		 */
		public boolean isFinished() {
			return finished;
		}

		/**
		 * 已经下载的内容大小
		 * @return 如果返回值为-1,代表下载失败
		 */
		public long getDownloadedLength() {
			return downloadedLength;
		}

	}












}


