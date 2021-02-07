package com.example.threaddownloads.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite��������ʵ�ִ������ݿ�ͱ����汾�仯ʱʵ�ֶԱ�����ݿ��Ĳ���
 * @author think
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "eric.db";	//�������ݿ������
	private static final int VERSION = 1;	//�������ݿ�İ汾
	
	/**
	 * ͨ�����췽��
	 * @param context Ӧ�ó���������Ķ���
	 */
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {	//�������ݱ�
		db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	//���汾�仯ʱϵͳ����øûص�����
		db.execSQL("DROP TABLE IF EXISTS filedownlog");	//�˴���ɾ�����ݱ���ʵ�ʵ�ҵ����һ������Ҫ���ݱ��ݵ�
		onCreate(db);	//����onCreate�������´������ݱ�Ҳ�����Լ�����ҵ����Ҫ�����µĵ����ݱ�
	}

}

