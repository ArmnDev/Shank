package com.mobiquel.urbanclap.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBAdapter extends SQLiteAssetHelper
{
	public static final String DB_NAME = "shank.db";
	public static final int DB_VERSION = 1;
	private SQLiteDatabase database;
	public static final String TABLE_LOCATION="location";
	public static final String COLUMN_ID="id";
	public static final String COLUMN_LAT="lat";
	public static final String COLUMN_LON="lon";
	public static final String COLUMN_IS_SYNCED="is_synced";
	private static DBAdapter instance;
	private DBAdapter(Context context) 
	{
		super(context, DB_NAME, null, DB_VERSION);
	}

	public synchronized static DBAdapter getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new DBAdapter(context);
		}
		return instance;
	}
	
	public SQLiteDatabase opendataBase()
	{
		database=getWritableDatabase();
		return database;
	}
}