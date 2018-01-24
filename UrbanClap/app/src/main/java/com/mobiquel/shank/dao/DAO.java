package com.mobiquel.shank.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobiquel.shank.adapter.DBAdapter;
import com.mobiquel.shank.model.LocationVO;

public class DAO 
{
	private SQLiteDatabase database;
	private DBAdapter dbAdapter;
	private static DAO instance;

	public DAO(Context c) 
	{
		this.dbAdapter = DBAdapter.getInstance(c);
	}
	
	public static DAO getInstance(Context context) 
	{
		if (instance == null) 
		{
			instance = new DAO(context);
		}
		return instance;
	}

	public void open() 
	{
		this.database = dbAdapter.getWritableDatabase();
	}
	
	public boolean addLocation(String lat,String lon,String id)
	{
		//Here routeId is tripId
		open();
		ContentValues values= new ContentValues();
		values.put(DBAdapter.COLUMN_LAT, lat);
		values.put(DBAdapter.COLUMN_LON, lon);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.format(new Date(System.currentTimeMillis()));
		System.out.println(new Date(System.currentTimeMillis()));
		values.put(DBAdapter.COLUMN_IS_SYNCED, "F");
		long l = database.insert(DBAdapter.TABLE_LOCATION, null, values);
		if(l!=-1)
			return true;
		else 
			return false;
	}

	public List<LocationVO> getLocations(String id)
	{
		open();
		LinkedList<LocationVO> locationList = new LinkedList<LocationVO>();
		open();
		Cursor cr = database.query(DBAdapter.TABLE_LOCATION,new String[] { DBAdapter.COLUMN_ID, DBAdapter.COLUMN_LAT,DBAdapter.COLUMN_LON},"is_synced = ?",new String[]{"F"},null, null, null);
		cr.moveToFirst();
		while (!cr.isAfterLast()) 
		{
			LocationVO l = new LocationVO();
			l.setId(cr.getString(cr.getColumnIndex(DBAdapter.COLUMN_ID)));
			l.setLat(cr.getString(cr.getColumnIndex(DBAdapter.COLUMN_LAT)));
			l.setLon(cr.getString(cr.getColumnIndex(DBAdapter.COLUMN_LON)));
			locationList.add(l);
			cr.moveToNext();
		}
		
		return locationList;
	}
	
	public void removeLocation(String id)
	{
		open();
		database.execSQL("Update " + DBAdapter.TABLE_LOCATION + " set is_synced='T' where id = " + id);
	}
}