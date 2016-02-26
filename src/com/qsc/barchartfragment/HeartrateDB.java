package com.qsc.barchartfragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HeartrateDB extends SQLiteOpenHelper{

	public HeartrateDB(Context context) {
		super(context, "heartratedb", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase heartratedb) {
		heartratedb.execSQL("CREATE TABLE myheartrate(id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "heartrate NUMBER DEFAULT NONE,time TIMESTAMP DEFAULT (datetime('now','localtime')))");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
